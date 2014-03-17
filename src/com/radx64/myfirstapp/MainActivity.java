package com.radx64.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
	private SensorManager mSensorManager;
	private Sensor mAcceleration;
	private Sensor mOrientation;
	private Sensor mGyroscope;
	
	private float[] gravity= new float[3];
	private float[] orientation = new float[3];
	private float[] gyroscope = new float[3];
	
	//UI
	private TextView editText;
	private TextView labelx1;
	private TextView labely1;
	private TextView labelz1;
	private ProgressBar pbarx;
	private ProgressBar pbary;
	private ProgressBar pbarz;
	
	private TextView labelRx1;
	private TextView labelRy1;
	private TextView labelRz1;
	private ProgressBar pbarRx;
	private ProgressBar pbarRy;
	private ProgressBar pbarRz;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        MyTimer timer = new MyTimer();
        timer.sendEmptyMessage(MyTimer.TIMER_1);
        
        editText = (TextView) findViewById(R.id.TextView1);
        labelx1 = (TextView) findViewById(R.id.labelx1);
        labely1 = (TextView) findViewById(R.id.labely1);
        labelz1 = (TextView) findViewById(R.id.labelz1);
        
        pbarx = (ProgressBar) findViewById(R.id.progressBarx);
        pbary = (ProgressBar) findViewById(R.id.progressBary);
        pbarz = (ProgressBar) findViewById(R.id.progressBarz);
        
        labelRx1 = (TextView) findViewById(R.id.labelRx1);
        labelRy1 = (TextView) findViewById(R.id.labelRy1);
        labelRz1 = (TextView) findViewById(R.id.labelRz1);
        
        pbarRx = (ProgressBar) findViewById(R.id.progressBarRx);
        pbarRy = (ProgressBar) findViewById(R.id.progressBarRy);
        pbarRz = (ProgressBar) findViewById(R.id.progressBarRz);
        pbarRx.setMax(360);
        pbarRy.setMax(360);
        pbarRz.setMax(360);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
      // Do something here if sensor accuracy changes.
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public final void onSensorChanged(SensorEvent event) {
    	// In this example, alpha is calculated as t / (t + dT),
    	  // where t is the low-pass filter's time-constant and
    	  // dT is the event delivery rate.
    	  
    	  switch(event.sensor.getType())
    	  {
    	  case Sensor.TYPE_ACCELEROMETER:
    	  	{
    	    	gravity = event.values.clone();
    	  		break;
    	  	}
    	  case Sensor.TYPE_ORIENTATION:
    	  	{
    		    orientation = event.values.clone();
    		    break;
    	  	}
    	  case Sensor.TYPE_GYROSCOPE:
    	  	{
    		  	gyroscope = event.values.clone();
    		  	break;
    	  	}
    	  }
    	    	  
    	  labelx1.setText(String.format("X:%03.2f",gravity[0]));
    	  labely1.setText(String.format("Y:%03.2f",gravity[1]));
    	  labelz1.setText(String.format("Z:%03.2f",gravity[2]));
    	  
    	  pbarx.setProgress((int)(Math.abs(gravity[0])*10.0));
    	  pbary.setProgress((int)(Math.abs(gravity[1])*10.0));
    	  pbarz.setProgress((int)(Math.abs(gravity[2])*10.0));
    	  
    	  labelRx1.setText(String.format("RX:%3.2f °",orientation[0]));
    	  labelRy1.setText(String.format("RY:%3.2f °",orientation[1]));
    	  labelRz1.setText(String.format("RZ:%3.2f °",orientation[2]));
    	  
    	  pbarRx.setProgress((int)(orientation[0]));
    	  pbarRy.setProgress((int)(orientation[1]));
    	  pbarRz.setProgress((int)(orientation[2]));
    }
    
    @Override 
    protected void onResume() {
      super.onResume();
      mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
      super.onPause();
      mSensorManager.unregisterListener(this);
    }
    
    public class MyTimer extends Handler
    {
    	public int counter = 0;
        public static final int TIMER_1 = 0;

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TIMER_1:
                    // Do something etc.
                    Log.d("TimerExample", "Timer 1");
                    sendEmptyMessageDelayed(TIMER_1, 100);
                    counter++;
                    editText.setText("Timer expired " + Integer.toString(counter) + " times");
                    break;
                default:
                    removeMessages(TIMER_1);
                    break;
            }
        }
    }
}
