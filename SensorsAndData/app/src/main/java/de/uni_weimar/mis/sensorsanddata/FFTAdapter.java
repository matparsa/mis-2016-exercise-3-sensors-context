package de.uni_weimar.mis.sensorsanddata;

import com.robinhood.spark.SparkAdapter;

/**
 * Created by matpa on 5/18/2016.
 */
public class FFTAdapter extends SparkAdapter {
    private double[] yData;

    public FFTAdapter(double[] yData) {
        this.yData = yData;
    }

    @Override
    public int getCount() {
        return yData.length;
    }

    @Override
    public Object getItem(int index) {
        return yData[index];
    }

    @Override
    public float getY(int index) {
        return ((float) yData[index]);
    }
}