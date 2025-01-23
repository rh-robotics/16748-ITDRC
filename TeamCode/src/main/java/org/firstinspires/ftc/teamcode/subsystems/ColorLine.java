package org.firstinspires.ftc.teamcode.subsystems;

public class ColorLine {
    private Color color;
    private int start, end, row, ref;

    public ColorLine(Color color, int start, int end, int row) {
        this.color = color;
        this.start = start;
        this.end = end;
        this.row = row;
        this.ref = -1;
    }

    public Color getColor() {
        return color;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getRow() {
        return row;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public boolean connects(ColorLine oCL) {
        boolean isSameColor = color == oCL.getColor();
        boolean canStack;

        if (start < oCL.getEnd() && start >= oCL.getStart()) {
            canStack = true;
        } else if (end > oCL.getStart() && end <= oCL.getEnd()) {
            canStack = true;
        } else if (oCL.getStart() < end && oCL.getStart() >= start) {
            canStack = true;
        } else if (oCL.getEnd() > start && oCL.getEnd() <= end) {
            canStack = true;
        } else {
            canStack = false;
        }

        return isSameColor && canStack;
    }
}
