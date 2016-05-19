package de.uni_weimar.mis.sensorsanddata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.robinhood.spark.SparkView;

import java.util.ArrayList;

/**
 * Created by matpa on 5/18/2016.
 */
public class ACCView extends View {
    public SensorObject sensorData;
    private Paint paint;
    public ACCView(Context context, AttributeSet attributeSet) {

        super(context, attributeSet);

    }
int i=0;
    @Override
    protected void onDraw(Canvas canvas) {
        paint = new Paint();
        float centerPoint =getMeasuredWidth()/2;
        paint.setStrokeWidth(8);
        paint.setTextSize(15);
        paint.setColor(Color.RED);
        canvas.drawText("x "+sensorData.x, 10, 25, paint);
        canvas.drawLine(centerPoint,25,(float)(centerPoint+sensorData.x*10),25,paint);
        paint.setColor(Color.GREEN);
        canvas.drawText("y "+sensorData.y, 10, 75, paint);
        canvas.drawLine(centerPoint,75,(float)(centerPoint+sensorData.y*10),75,paint);
        paint.setColor(Color.BLUE);
        canvas.drawText("z "+sensorData.z, 10, 125, paint);
        canvas.drawLine(centerPoint,125,(float)(centerPoint+sensorData.z*10),125,paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("magnitude ", 10, 175, paint);
        canvas.drawLine(centerPoint,175,(float)(centerPoint+sensorData.omegaG*10),175,paint);
    }

}






