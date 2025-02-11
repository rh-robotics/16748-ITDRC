package org.firstinspires.ftc.teamcode.subsystems;

import java.util.ArrayList;

public class ColorBlock {
    private ArrayList<ColorLine> lines;
    private int segSize; // segment size

    public ColorBlock(int segSize) {
        lines = new ArrayList<>();
        this.segSize = segSize;
    }

    public void add(ColorLine line) {
        lines.add(line);
    }

    public ArrayList<ColorLine> getLines() {
        return lines;
    }

    public void add(ColorBlock block) {
        lines.addAll(block.getLines());
    }

    public void clear() {
        lines.clear();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int size() {
        return lines.size();
    }

    public Point2[] getVertices() {
        if (lines.isEmpty()) {
            return null;
        }

        Point2[] ret = new Point2[4];

        int yMin = 999999;
        int xMinMin = 999999;
        int xMinMax = -1;

        int yMax = -1;
        int xMaxMin = 999999;
        int xMaxMax = -1;

        for (int i = 0; i < lines.size(); i++) {
            ColorLine line = lines.get(i);

            if (line.getRow() * segSize < yMin) {
                yMin = line.getRow() * segSize;

                xMinMin = line.getStart() * segSize;
                xMinMax = line.getEnd() * segSize;
            } else if (line.getRow() * segSize == yMin) {
                if (line.getStart() * segSize < xMinMin) {
                    xMinMin = line.getStart() * segSize;
                } else if (line.getEnd() * segSize > xMinMax) {
                    xMinMax = line.getEnd() * segSize;
                }
            }

            if (line.getRow() * segSize > yMax) {
                yMax = line.getRow() * segSize;

                xMaxMin = line.getStart() * segSize;
                xMaxMax = line.getEnd() * segSize;
            } else if (line.getRow() * segSize == yMax) {
                if (line.getStart() * segSize < xMaxMin) {
                    xMaxMin = line.getStart() * segSize;
                } else if (line.getEnd() * segSize > xMaxMax) {
                    xMaxMax = line.getEnd() * segSize;
                }
            }
        }

        ret[0] = new Point2(xMinMin, yMin);
        ret[1] = new Point2(xMinMax, yMin);
        ret[2] = new Point2(xMaxMin, yMax);
        ret[3] = new Point2(xMaxMax, yMax);

        return ret;
    }
}
