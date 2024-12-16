package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.subsystems.PixelCluster;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Locale;

@TeleOp(name = "VC", group = "Iterative OpMode")
public class VC extends OpMode {
    OpenCvWebcam webcam;
    ObjRecPipeline pipeline = new ObjRecPipeline(10);

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
        PixelCluster[][] clusters = pipeline.getClusters();
        String out = "";

        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].length; j++) {
                out += clusters[i][j].getCClass();
            }
            out += "\n";
        }

        telemetry.addLine(out);
        telemetry.update();
    }

    class ObjRecPipeline extends OpenCvPipeline {
        private boolean viewportPaused;
        private final int segments;
        private PixelCluster[][] clusters;

        // Width and height must be divisible by segment
        public ObjRecPipeline(int segments) {
            this.viewportPaused = false;
            this.segments = segments;
            this.clusters = null;
        }

        @Override
        public Mat processFrame(Mat input) {
            clusters = PixelCluster.pixelify(input, segments);
            setClusterFlags();
            classifyClusters();
            return input;
        }

        public PixelCluster[][] getClusters() {
            return clusters;
        }

        public void setClusterFlags() {
            for (int i = 0; i < clusters.length; i++) {
                for (int j = 0; j < clusters[i].length - 1; j++) {
                    double[] contrast = new double[4];

                    for (int k = 0; i < 4; i++) {
                        contrast[k] = clusters[i][j+1].getRgba()[k] - clusters[i][j].getRgba()[k];
                    }

                    int flag = genFlag(contrast);
                    if (flag == 7 && clusters[i][j].getFlag() == 0) {
                        flag = 0;
                    }
                    clusters[i][j+1].setFlag(flag);
                }
            }
        }

        public int genFlag(double[] contrast) {
            int tolerance = 50;

            if ((contrast[0] >= tolerance && contrast[1] <= tolerance) || (contrast[1] <= tolerance && contrast[0] >= -tolerance)) {
                return 2;
            } else if (contrast[1] >= tolerance && contrast[2] <= tolerance) {
                return 4;
            } else if (contrast[2] >= tolerance && contrast[1] <= tolerance && contrast[0] <= tolerance) {
                return 6;
            } else if (contrast[0] <= tolerance && contrast[1] >= -tolerance) {
                return 1;
            } else if (contrast[0] <= tolerance && contrast[1] <= tolerance) {
                return 3;
            } else if (contrast[2] <= tolerance) {
                return 5;
            } else {
                return 7;
            }
        }

        public void classifyClusters() {
            for (int i = 0; i < clusters.length; i++) {
                int lsf = 0; // last significant flag
                for (int j = 1; j < clusters[i].length; j++) {
                    int flag = clusters[i][j].getFlag();
                    if (lsf == 0 && flag != 7 && flag % 2 == 1) {
                        backClassify(i, j, flag+1);
                    } else if (flag != 7) {
                        lsf = flag;
                    }

                    clusters[i][j].setCClass(getCChar(flag));
                }
            }
        }

        public char getCChar(int flag) {
            if (flag == 2) {
                return 'R';
            } else if (flag == 4) {
                return 'Y';
            } else if (flag == 6) {
                return 'B';
            } else {
                return 'O';
            }
        }

        public void backClassify(int sRow, int sCol, int flag) {
            char cChar = getCChar(flag);

            for (int col = sCol; col >= 0; col--) {
                clusters[sRow][col].setCClass(cChar);
            }
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
    }
}
