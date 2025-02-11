package org.firstinspires.ftc.teamcode.subsystems;

import java.util.ArrayList;

public class ColorBlock {
    private ArrayList<ColorLine> lines;
    private int segSize; // segment size
    private Color color;

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

    public void setColor() {
        color = lines.get(0).getColor();
    }

    public Color getColor() {
        return color;
    }

    public Point2[] getVertices() {
        if (lines.isEmpty()) {
            return null;
        }

        Point2[] ret = new Point2[4];

        int yMin = 999999;
        int yMinXMin = 999999; // minimum x value corresponding to yMin value
        int yMinXMax = -1; // maximum x value corresponding to yMax value

        int yMax = -1;
        int yMaxXMin = 999999; // minimum x value corresponding to yMax value
        int yMaxXMax = -1; // maximum x value corresponding to yMax value

        for (int i = 0; i < lines.size(); i++) {
            ColorLine line = lines.get(i);

            if (line.getRow() * segSize < yMin) {
                yMin = line.getRow() * segSize;

                yMinXMin = line.getStart() * segSize;
                yMinXMax = line.getEnd() * segSize;
            } else if (line.getRow() * segSize == yMin) {
                if (line.getStart() * segSize < yMinXMin) {
                    yMinXMin = line.getStart() * segSize;
                } else if (line.getEnd() * segSize > yMinXMax) {
                    yMinXMax = line.getEnd() * segSize;
                }
            }

            if (line.getRow() * segSize > yMax) {
                yMax = line.getRow() * segSize;

                yMaxXMin = line.getStart() * segSize;
                yMaxXMax = line.getEnd() * segSize;
            } else if (line.getRow() * segSize == yMax) {
                if (line.getStart() * segSize < yMaxXMin) {
                    yMaxXMin = line.getStart() * segSize;
                } else if (line.getEnd() * segSize > yMaxXMax) {
                    yMaxXMax = line.getEnd() * segSize;
                }
            }
        }

        ret[0] = new Point2(yMinXMin, yMin);
        ret[1] = new Point2(yMinXMax, yMin);
        ret[2] = new Point2(yMaxXMin, yMax);
        ret[3] = new Point2(yMaxXMax, yMax);

        return ret;
    }
}
