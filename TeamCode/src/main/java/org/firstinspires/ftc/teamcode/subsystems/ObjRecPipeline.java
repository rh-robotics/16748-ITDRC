package org.firstinspires.ftc.teamcode.subsystems;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class ObjRecPipeline extends OpenCvPipeline {
    private boolean viewportPaused;
    private final int sectionSize;
    private final int samples;
    private ColorLine[][] grid;
    private ArrayList<ColorBlock> blocks;
    private int refC;
    private ArrayList<Integer> merge;

    public ObjRecPipeline(int sectionSize, int samples) {
        this.viewportPaused = false;
        this.sectionSize = sectionSize;
        this.samples = samples;
    }

    public Mat processFrame(Mat input) {
        genGrid(input);
        constructBlocks();

        return input;
    }

    public ArrayList<ColorBlock> getBlocks() {
        return blocks;
    }

    public int[][] getVertices() {
        ArrayList<Integer> r = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();
        ArrayList<Integer> b = new ArrayList<Integer>();

        for (int i = 0; i < blocks.size(); i++) {
            ColorBlock block = blocks.get(i);
            if (block.getColor() == Color.RED) {
                for (int n : block.getVertices()) {
                    r.add(n);
                }
            } else if (block.getColor() == Color.YELLOW) {
                for (int n : block.getVertices()) {
                    y.add(n);
                }
            } else if (block.getColor() == Color.BLUE) {
                for (int n : block.getVertices()) {
                    b.add(n);
                }
            }
        }

        int[][] ret = new int[3][];
        ret[0] = new int[r.size()];
        ret[1] = new int[y.size()];
        ret[2] = new int[b.size()];

        ret[0] = r.stream().mapToInt(i -> i).toArray();
        ret[1] = y.stream().mapToInt(i -> i).toArray();
        ret[2] = b.stream().mapToInt(i -> i).toArray();

        return ret;
    }

    private void genGrid(Mat mat) {
        grid = new ColorLine[mat.rows() / sectionSize][];

        for (int hSec = 0; hSec < mat.rows() / sectionSize; hSec++) {
            grid[hSec] = processHorizontalSection(mat, hSec);
        }
    }

    private ColorLine[] processHorizontalSection(Mat mat, int hSec) {
        int yMin = hSec * sectionSize;
        ArrayList<ColorLine> list = new ArrayList<>();

        Color lColor = colorSample(mat, 0, yMin); // Last color
        int start = 0;

        for (int vSec = 1; vSec < (mat.cols() / sectionSize); vSec++) {
            Color cColor = colorSample(mat, vSec * sectionSize, yMin); // Current color

            if (lColor != Color.OTHER && cColor != lColor) {
                ColorLine colorLine = new ColorLine(lColor, start, vSec, hSec);
                list.add(colorLine);
                lColor = cColor;
                start = vSec;
            } else if (cColor != lColor) {
                lColor = cColor;
                start = vSec;
            }
        }

        if (lColor != Color.OTHER) {
            ColorLine colorLine = new ColorLine(lColor, start, mat.cols() / sectionSize, hSec);
            list.add(colorLine);
        }

        ColorLine[] ret = new ColorLine[list.size()];
        list.toArray(ret);

        return ret;
    }

    private Color colorSample(Mat mat, int xMin, int yMin) {
        double[] totals = {0, 0, 0, 0};

        for (int i = 0; i < samples; i++) {
            int xRand = (int) (sectionSize * Math.random()) + xMin;
            int yRand = (int) (sectionSize * Math.random()) + yMin;

            double[] rgba = mat.get(yRand, xRand);

            for (int j = 0; j < 4; j++) {
                totals[j] += rgba[j];
            }
        }

        double[] avgs = new double[4];
        for (int i = 0; i < 4; i++) {
            avgs[i] = totals[i] / samples;
        }

        return ColorHelper.classify(avgs);
    }

    private void constructBlocks() {
        refC = 0; // Ref count
        blocks = new ArrayList<>();
        merge = new ArrayList<>(); // Refs to merge

        for (int col = 0; col < grid[0].length; col++) {
            grid[0][col].setRef(refC);
            refC++;

            blocks.add(new ColorBlock(sectionSize));
            blocks.get(col).add(grid[0][col]);
        }

        for (int row = 1; row < grid.length; row++) {
            matchLines(row);
        }

        // Merge
        for (int i = 0; i < merge.size(); i+=2) {
            int mergeTo = merge.get(i);
            int mergeFrom = merge.get(i+1);

            blocks.get(mergeTo).add(blocks.get(mergeFrom));
            blocks.get(mergeFrom).clear();
        }

        // Clean
        for (int i = blocks.size() - 1; i >= 0; i--) {
            if (blocks.get(i).isEmpty() || blocks.get(i).size() < 4 * sectionSize / 10) {
                blocks.remove(i);
            }
        }

        // Colorize
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).setColor();
        }
    }

    private void matchLines(int row) {
        for (int col1 = 0; col1 < grid[row].length; col1++) {
            for (int col2 = 0; col2 < grid[row-1].length; col2++) {
                if (grid[row][col1].connects(grid[row-1][col2]) && grid[row][col1].getRef() == -1) {
                    int ref = grid[row-1][col2].getRef();
                    grid[row][col1].setRef(ref);
                    blocks.get(ref).add(grid[row][col1]);
                } else if (grid[row][col1].connects(grid[row-1][col2])) {
                    int x = grid[row][col1].getRef(); // Ref to merge into
                    int y = grid[row-1][col2].getRef(); // Ref to be merged

                    merge.add(x);
                    merge.add(y);
                }
            }

            if (grid[row][col1].getRef() == -1) {
                grid[row][col1].setRef(refC);
                blocks.add(new ColorBlock(sectionSize));
                blocks.get(refC).add(grid[row][col1]);

                refC++;
            }
        }
    }
}
