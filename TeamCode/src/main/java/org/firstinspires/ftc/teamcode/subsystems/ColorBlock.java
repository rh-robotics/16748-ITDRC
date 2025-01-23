package org.firstinspires.ftc.teamcode.subsystems;

import java.util.ArrayList;

public class ColorBlock {
    private ArrayList<ColorLine> lines;

    public ColorBlock() {
        lines = new ArrayList<>();
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
}
