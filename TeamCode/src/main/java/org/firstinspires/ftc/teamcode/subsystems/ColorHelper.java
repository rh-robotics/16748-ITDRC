package org.firstinspires.ftc.teamcode.subsystems;

public class ColorHelper {
    public static Color classify(double[] rgba) {
        double red = rgba[0];
        double green = rgba[1];
        double blue = rgba[2];

        double redProp = red / (red + green + blue);
        double greenProp = green / (red + green + blue);
        double blueProp = blue / (red + green + blue);

        if (redProp > 0.45 && greenProp < 0.35) {
            return Color.RED;
        } else if (redProp > 0.45 && greenProp > 0.35) {
            return Color.YELLOW;
        } else if (blueProp > 0.45) {
            return Color.BLUE;
        } else {
            return Color.OTHER;
        }
    }
}
