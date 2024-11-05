package org.firstinspires.ftc.teamcode.subsystems;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

class TestPipeline extends OpenCvPipeline
{
    @Override
    public Mat processFrame(Mat input)
    {
        return input;
    }
}