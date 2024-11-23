package org.firstinspires.ftc.teamcode.teleop;

import android.content.Context;
import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.subsystems.HWC;
import org.firstinspires.ftc.teamcode.subsystems.TfliteInterpreter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@TeleOp(name = "TFLITE TEST", group = "Iterative OpMode")

public class TfliteTest extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    HWC robot;
    OpenCvWebcam webcam;
    final int width = 320;
    final int height = 240;
    TflitePipeline pipeline;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot = new HWC(hardwareMap, telemetry);
        pipeline = new TflitePipeline(hardwareMap.appContext, width, height);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().
                getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"),
                cameraMonitorViewId);
        webcam.setPipeline(pipeline);
        webcam.setMillisecondsPermissionTimeout(5000);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(width, height, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Error", String.valueOf(errorCode));
            }
        });
        telemetry.update();
    }

    @Override
    public void init_loop() {
        telemetry.addData("Frame Count", webcam.getFrameCount());
        telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
        telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
        telemetry.update();
    }

    @Override
    public void start() {
        time.reset();
        telemetry.addData("Status", "Starting");
        telemetry.update();
    }

    @Override
    public void loop() {
        float[] results = pipeline.getLastResult();
        if (results != null) {
            telemetry.addData("TF", Arrays.toString(results));
            telemetry.update();
        }
    }
}

class TflitePipeline extends OpenCvPipeline {
    float[] lastResult;
    TfliteInterpreter interpreter = new TfliteInterpreter(13);
    Context context;
    IOException err;
    boolean wasErr;
    int width;
    int height;

    public TflitePipeline(Context context, int width, int height) {
        this.lastResult = null;
        this.context = context;
        this.err = null;
        this.wasErr = false;
        this.width = width;
        this.height = height;

        try {
            interpreter.loadModel(context, "model.tflite");
        } catch (IOException e) {
            err = e;
            wasErr = true;
        }

        /*if (!wasErr) {
            try {
                interpreter.loadLabels(context);
            } catch (IOException e) {
                err = e;
                wasErr = true;
            }
        }*/
    }

    @Override
    public Mat processFrame(Mat input) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(input, bitmap);
        interpreter.runModel(bitmap);
        lastResult = interpreter.process();

        return input;
    }

    public float[] getLastResult() {
        return lastResult;
    }

    public IOException getErr() {
        return err;
    }
}
