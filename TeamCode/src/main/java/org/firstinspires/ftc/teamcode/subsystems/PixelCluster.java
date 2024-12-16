package org.firstinspires.ftc.teamcode.subsystems;

import org.opencv.core.Mat;

public class PixelCluster {
    private double[] rgba;
    private double[] center;
    private int flag;
    private char cClass;

    public PixelCluster(double[] color, double[] center) {
        this.rgba = color;
        this.center = center;
        this.flag = 0; // (0: S, 1: R-, 2: R+, 3: Y-, 4: Y+, 5: B-, 6: B+, 7: 0)
        this.cClass = 'N';
    }

    public double[] getRgba() {
        return rgba;
    }

    public double[] getCenter() {
        return center;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setCClass(char cClass) {
        this.cClass = cClass;
    }

    public char getCClass() {
        return cClass;
    }

    public static PixelCluster[][] pixelify(Mat mat, int segments) {
        PixelCluster[][] clusters = new PixelCluster[segments][segments];

        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < segments; j++) {
                int[] s = {mat.rows() / segments * i, mat.cols() / segments * j};
                int[] e = {mat.rows() / 10 * (i+1) - 1, mat.cols() / 10 * (j+1) - 1};
                clusters[i][j] = clusterRect(mat, s, e);
            }
        }

        return clusters;
    }

    // TODO: MAKE NAME NOT STUPID
    public static PixelCluster clusterRect(Mat mat, int[] s, int[] e) {
        PixelCluster pix;
        double[] center = new double[2];
        center[0] = (e[0] - s[0]) / 2.0;
        center[1] = (e[1] - s[1]) / 2.0;

        int count = 5;
        double[] rgba = new double[4];

        for (int i = 0; i < count; i++) {
            int rx = (int) (Math.random() * (e[0] - s[0]) + s[0]);
            int ry = (int) (Math.random() * (e[1] - s[1]) + s[0]);

            for (int j = 0; j < rgba.length; j++) {
                rgba[j] += mat.get(rx, ry)[j];
            }
        }

        for (int i = 0; i < rgba.length; i++) {
            rgba[i] /= count;
        }

        pix = new PixelCluster(rgba, center);

        return pix;
    }
}
