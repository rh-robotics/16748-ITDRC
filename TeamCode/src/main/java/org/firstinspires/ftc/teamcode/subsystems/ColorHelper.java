package org.firstinspires.ftc.teamcode.subsystems;

public class ColorHelper {
    public static Color classify(double[] rgba) {
        double red = rgba[0];
        double green = rgba[1];
        double blue = rgba[2];

        if (red > 200 && green < 200 && blue < 200) {
            return Color.RED;
        } else if (red > 200 && green > 200 && blue < 200) {
            return Color.YELLOW;
        } else if (red < 200 && green < 200 && blue > 200) {
            return Color.BLUE;
        } else {
            return Color.OTHER;
        }
    }
}
