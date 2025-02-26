package org.firstinspires.ftc.teamcode.teleop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ObjRecPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

import java.util.Locale;

@TeleOp(name = "Computer Vision Demo", group = "Iterative OpMode")
public class ComputerVisionDemo extends OpMode {
    OpenCvCamera camera;
    ObjRecPipeline pipeline = new ObjRecPipeline(10, 20);

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        camera.setPipeline(pipeline);

        // camera.setMillisecondsPermissionTimeout(5000);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
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
        telemetry.addData("Frame Count", camera.getFrameCount());
        telemetry.addData("FPS", String.format(Locale.ENGLISH, "%.2f", camera.getFps()));
        telemetry.addData("Total frame time ms", camera.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", camera.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", camera.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", camera.getCurrentPipelineMaxFps());
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
        int[][] vertices = new int[3][];
        vertices = pipeline.getVertices();

        String r = "";
        for (int n : vertices[0]) {
            r += Integer.toString(n);
            r += ", ";
        }

        String y = "";
        for (int n : vertices[1]) {
            y += Integer.toString(n);
            y += ", ";
        }

        String b = "";
        for (int n : vertices[2]) {
            b += Integer.toString(n);
            b += ", ";
        }

        telemetry.addData("Red", r);
        telemetry.addData("Yellow", y);
        telemetry.addData("Blue", b);
    }
}