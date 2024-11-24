package org.firstinspires.ftc.teamcode.subsystems;


import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class TfliteInterpreter {
    TensorImage tImage;
    TensorBuffer probabilityBuffer;
    MappedByteBuffer tfliteModel;
    InterpreterApi tflite;

    public TfliteInterpreter() {
        tImage = new TensorImage(DataType.FLOAT32);
        probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, 13}, DataType.FLOAT32);
        tfliteModel = null;
        tflite = null;
    }

    public void initialiseModel(Context context, String file) throws IOException {
        tfliteModel = FileUtil.loadMappedFile(context, file);
        tflite = InterpreterApi.create(tfliteModel, new InterpreterApi.Options());
    }

    public void loadImage(Bitmap bitmap) {
        tImage.load(bitmap);
    }

    public void runInference() throws NullPointerException {
        tflite.run(tImage.getBuffer(), probabilityBuffer.getBuffer());
    }

    public float[] getResult() {
        return probabilityBuffer.getFloatArray();
    }

}
