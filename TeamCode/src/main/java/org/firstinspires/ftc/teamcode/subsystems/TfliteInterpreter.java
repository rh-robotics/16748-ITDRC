package org.firstinspires.ftc.teamcode.subsystems;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;

import org.tensorflow.lite.InterpreterApi;
public class TfliteInterpreter {
    public static final String ASSOCIATED_AXIS_LABELS = "labels.txt";
    private TensorBuffer probabilityBuffer;
    private InterpreterApi tflite;
    private List<String> associatedAxisLabels;

    public TfliteInterpreter() {
        probabilityBuffer = TensorBuffer.createFixedSize(
                new int[]{1, 1001}, DataType.UINT8);
        tflite = null;
        associatedAxisLabels = null;
    }
    public TfliteInterpreter(int n) {
        probabilityBuffer = TensorBuffer.createFixedSize(
                new int[]{1, n}, DataType.UINT8);
        tflite = null;
        associatedAxisLabels = null;
    }

    public void loadModel(Context context, String file) throws IOException{
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, file);
            InterpreterApi tflite = InterpreterApi.create(
                    tfliteModel, new InterpreterApi.Options());
    }

    public void loadLabels(Context context) throws IOException {
        associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS);
    }

    public int runModel(Bitmap bitmap) {
        if (tflite == null) {
            return 1;
        }

        TensorImage tImage = TensorImage.fromBitmap(bitmap);

        tflite.run(tImage.getBuffer(), probabilityBuffer.getBuffer());
        return 0;
    }

    public float[] process() {
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder()
                .add(new NormalizeOp(0, 255)).build();

        /*if (associatedAxisLabels != null) {
            TensorLabel labels = new TensorLabel(associatedAxisLabels,
                    probabilityProcessor.process(probabilityBuffer));

            return labels.getMapWithFloatValue();
        }*/

        return probabilityBuffer.getFloatArray();

        // return null;
    }

}
