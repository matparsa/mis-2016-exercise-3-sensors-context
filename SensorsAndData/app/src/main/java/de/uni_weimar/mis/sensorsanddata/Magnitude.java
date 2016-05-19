package de.uni_weimar.mis.sensorsanddata;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matpa on 5/17/2016.
 */
class SensorObject
{
    public double x = 0.0d;
    public double y = 0.0d;
    public double z = 0.0d;
    public double omegaG= 0.0d;
    public SensorObject(double x, double y, double z,double omegaG){
        this.x = x;
        this.y = y;
        this.z = z;
        this.omegaG = omegaG;
    }

}
public class Magnitude {
    public ArrayList<SensorObject> sensors=new ArrayList<SensorObject>();
    public double[] _FFTResult;
    public SensorObject sensorData=new SensorObject(0.0d,0.0d,0.0d,0.0d);
    private FFT _FFT;
    private int winSize;
    public void setWindowSize(int windowSize)
    {
        winSize = (int)Math.pow(2,windowSize);
        sensors.clear();
        _FFT = new FFT(winSize);
        for(int i=0 ; i < winSize ; i++)
            sensors.add(new SensorObject(0.0d,0.0d,0.0d,0.0d));
        CalculateFFT();
    }

    public double[] calculateMagnitude(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sensorData.x = (double) event.values[0];
            sensorData.y = (double) event.values[1];
            sensorData.z = (double) event.values[2];
            sensorData.omegaG = Math.sqrt((sensorData.x * sensorData.x) + (sensorData.y * sensorData.y) + (sensorData.z * sensorData.z));
            if(winSize <= sensors.size())
                sensors.remove(0);
            sensors.add(new SensorObject(sensorData.x,sensorData.y,sensorData.z,sensorData.omegaG));
            CalculateFFT();
            }
            return _FFTResult;
    }

    private void CalculateFFT() {
        double[] actual = new double[winSize];
        double[] imaginary = new double[winSize];
        for (int i = 0; i < winSize; i++)
            actual[i] = sensors.get(i).omegaG;
        Arrays.fill(imaginary, 0.0d);
        _FFT = new FFT(winSize);
        _FFT.fft(actual, imaginary);
        _FFTResult = new double[winSize];
        for (int i = 0; i < winSize; i++)
            _FFTResult[i] = Math.sqrt(actual[i] * actual[i] + imaginary[i] * imaginary[i]);
    }
    public String GetActivity()
    {
        //We calculated the average of  _FFTResult data and then recognize the activity
        double average = 0.0d;
        for(int i = 0 ; i < winSize ; i++){
            average += _FFTResult[i];
        }
        average = average / winSize;
        String activity="";
        if(average < 10.0){
            activity = "sitting";
        }
        else if(average < 15.0) {
            activity = "walking";
        }
        else {
            activity = "running";
        }
        return activity;
    }

}
