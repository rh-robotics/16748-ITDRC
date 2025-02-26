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

    /**
     *
     * @return List of color block's vertices: indexes 0 and 1 are the x and y values of the top
     * vertex respectively, indexes 2 and 3 are x and y values of the right vertex, 4 and 5 are x and
     * y values of bottom vertex, and 6 and 7 are x and y of left vertex
     */
    public int[] getVertices() {
        if (lines.isEmpty()) {
            return null;
        }

        int[] ret = { 0, 999999, -1, 0, 0, -1, 999999, 0 };

        for (ColorLine line : lines) {
            // top (left priority)
            if (line.getRow() < ret[1]) {
                ret[0] = segSize * line.getStart();
                ret[1] = segSize * line.getRow();
            } else if (line.getRow() == ret[1] && line.getStart() < ret[0]) {
                ret[0] = segSize * line.getStart();
            }

            // right (top priority)
            if (line.getEnd() > ret[2]) {
                ret[2] = segSize * line.getEnd();
                ret[3] = segSize * line.getRow();
            } else if (line.getEnd() == ret[2] && line.getRow() < ret[3]) {
                ret[3] = segSize * line.getRow();
            }

            // bottom (right priority)
            if (line.getRow() > ret[5]) {
                ret[4] = segSize * line.getEnd();
                ret[5] = segSize * line.getRow();
            } else if (line.getRow() == ret[5] && line.getEnd() > ret[4]) {
                ret[4] = segSize * line.getEnd();
            }

            // left (bottom priority)
            if (line.getStart() < ret[6]) {
                ret[6] = segSize * line.getStart();
                ret[7] = segSize * line.getRow();
            } else if (line.getStart() == ret[6] && line.getRow() > ret[7]) {
                ret[7] = segSize * line.getRow();
            }
        }

        return ret;
    }
}
