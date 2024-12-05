package org.firstinspires.ftc.teamcode.teleop;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@TeleOp(name = "OPENCV", group = "Iterative OpMode")
public class OpenCVTest extends OpMode {
    OpenCvWebcam webcam;
    TestPipeline pipeline = new TestPipeline();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class,
                "Webcam"), cameraMonitorViewId);

        webcam.setPipeline(pipeline);

        webcam.setMillisecondsPermissionTimeout(5000);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                // Called if camera could not be opened
            }
        });

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        telemetry.addData("Frame Count", webcam.getFrameCount());
        telemetry.addData("FPS", String.format(Locale.ENGLISH, "%.2f", webcam.getFps()));
        telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
        telemetry.update();
    }

    @Override
    public void start() {
        telemetry.addData("Status", "Starting");
        telemetry.update();

        telemetry.addData("Status", "Started");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (pipeline.getBricks() != null && !pipeline.getBricks().isEmpty()) {
            telemetry.addData("Obj Rec", pipeline.getBricks().get(0));
        }
        telemetry.update();
    }

    class TestPipeline extends OpenCvPipeline {
        boolean viewportPaused;
        Map<Point, Color> colorMap;
        ArrayList<Brick> bricks;
        ArrayList<Point> cache;
        int[] sum;
        int count;

        public TestPipeline() {
            viewportPaused = false;
            colorMap = new HashMap<Point, Color>();
            bricks = new ArrayList<Brick>();
            cache = null;
            sum = null;
            count = 0;
        }

        @Override
        public Mat processFrame(Mat input) {
            generateColorMap(input);

            while (!colorMap.isEmpty()) {
                Point[] keys = new Point[colorMap.size()];
                colorMap.keySet().toArray(keys);
                Point point = keys[0];
                Brick brick = brickify(point);
                bricks.add(brick);
            }

            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;

            if (viewportPaused) {
                webcam.pauseViewport();
            } else {
                webcam.resumeViewport();
            }
        }

        public ArrayList<Brick> getBricks() {
            return bricks;
        }

        public Color classify(double[] rgba) {
            final int TOLERANCE = 200;

            if (rgba.length != 4) {
                return Color.OTHER;
            }

            if (rgba[0] >= TOLERANCE && rgba[1] < TOLERANCE && rgba[2] < TOLERANCE) {
                return Color.RED;
            } else if (rgba[0] >= TOLERANCE && rgba[1] >= TOLERANCE && rgba[2] < TOLERANCE) {
                return Color.YELLOW;
            } else if (rgba[0] < TOLERANCE && rgba[1] < TOLERANCE && rgba[2] >= TOLERANCE) {
                return Color.BLUE;
            } else {
                return Color.OTHER;
            }
        }

        public void generateColorMap(Mat mat) {
            colorMap.clear();

            for (int x = 0; x < mat.rows(); x++) {
                for (int y = 0; y < mat.cols(); y++) {
                    double[] rgba = mat.get(x, y);
                    Color color = classify(rgba);

                    if (color != Color.OTHER) {
                        Point k = new Point(x, y);
                        colorMap.put(k, color);
                    }
                }
            }
        }

        public Brick brickify(Point point) {
            cache = new ArrayList<Point>();
            sum = new int[2];
            count = 0;
            Color color = colorMap.get(point);
            int[] direction = new int[]{0, 0};

            brickPropagate(point, direction, color);

            int xCenter = sum[0] / count;
            int yCenter = sum[1] / count;
            Point centroid = new Point(xCenter, yCenter);
            Point[] points = new Point[cache.size()];

            for (int i = 0; i < points.length; i++) {
                points[i] = cache.get(i);
            }

            return new Brick(centroid, points, color);
        }

        public void brickPropagate(Point point, int[] direction, Color color) {
            if (colorMap.get(point) != null && colorMap.get(point) == color) {
                cache.add(point);
                sum[0] += point.getX();
                sum[1] += point.getY();
                count++;
                colorMap.remove(point);
            } else {
                return;
            }

            int encode = (direction[0] + 1) * 4 + (direction[0] + 1);
            switch(encode) {
                case(0): // direction = [-1, -1]
                    brickPropagate(new Point(point.getX() - 1, point.getY() - 1), new int[] {-1, -1}, color);
                    brickPropagate(new Point(point.getX() - 1, point.getY()), new int[] {-1, 0}, color);
                    brickPropagate(new Point(point.getX(), point.getY() - 1), new int[] {0, -1}, color);
                case(1): // direction = [-1, 0]
                    brickPropagate(new Point(point.getX() - 1, point.getY()), new int[] {-1, 0}, color);
                case(2): // direction = [-1, 1]
                    brickPropagate(new Point(point.getX() - 1, point.getY() + 1), new int[] {-1, 1}, color);
                    brickPropagate(new Point(point.getX() - 1, point.getY()), new int[] {-1, 0}, color);
                    brickPropagate(new Point(point.getX(), point.getY() + 1), new int[] {0, 1}, color);
                case(4): // direction = [0, -1]
                    brickPropagate(new Point(point.getX(), point.getY() - 1), new int[] {0, -1}, color);
                case(5): // direction = [0, 0]
                    brickPropagate(new Point(point.getX() - 1, point.getY() - 1), new int[] {-1, -1}, color);
                    brickPropagate(new Point(point.getX() - 1, point.getY()), new int[] {-1, 0}, color);
                    brickPropagate(new Point(point.getX() - 1, point.getY() + 1), new int[] {0, 1}, color);
                    brickPropagate(new Point(point.getX(), point.getY() - 1), new int[] {0, -1}, color);
                    brickPropagate(new Point(point.getX(), point.getY() + 1), new int[] {0, 1}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY() - 1), new int[] {1, -1}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY()), new int[] {1, 0}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY() + 1), new int[] {1, 1}, color);
                case(6): // direction = [0, 1]
                    brickPropagate(new Point(point.getX(), point.getY() + 1), new int[] {0, 1}, color);
                case(8): // direction = [1, -1]
                    brickPropagate(new Point(point.getX(), point.getY() - 1), new int[] {0, -1}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY() - 1), new int[] {1, -1}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY()), new int[] {1, 0}, color);
                case(9): // direction = [1, 0]
                    brickPropagate(new Point(point.getX() + 1, point.getY()), new int[] {1, 0}, color);
                case(10): // direction = [1, 1]
                    brickPropagate(new Point(point.getX(), point.getY() + 1), new int[] {0, 1}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY()), new int[] {1, 0}, color);
                    brickPropagate(new Point(point.getX() + 1, point.getY() + 1), new int[] {1, 1}, color);
            }
        }
    }

    public class Point {
        private int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @NonNull
        public String toString() {
            return "Centroid: " + "(" + this.x + ", " + this.y + ")";
        }
    }

    public class Brick {
        private Point centroid;
        private Point[] points;
        Color color;

        public Brick(Point centroid, Point[] points, Color color) {
            this.centroid = centroid;
            this.points = points;
            this.color = color;
        }

        @NonNull
        public String toString() {
            return "Brick with color " + color.name() + " and centroid: " + centroid.toString();
        }
    }
}
