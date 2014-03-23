package com.radx64.aSensors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.radx64.myfirstapp.R;

public class MainActivity extends Activity implements SensorEventListener{
	private SensorManager mSensorManager;
	private Sensor mAcceleration;
	private Sensor mOrientation;
	private Sensor mGyroscope;
	
	private float[] gravity= new float[3];
	private float[] orientation = new float[3];
	private float[] gyroscope = new float[3];
	
	ArrayList<String> Logs = new ArrayList<String>();
	
	private int positionMark = 1;
	
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
	
	private TextView labelGx1;
	private TextView labelGy1;
	private TextView labelGz1;
	private ProgressBar pbarGx;
	private ProgressBar pbarGy;
	private ProgressBar pbarGz;
	
	private ToggleButton tbutton;
	private File datafile;
	private MyTimer timer;
	private Calendar calendar;

	
	public void markPosition(View view)
	{
		Logs.add("Position mark "+ positionMark);
		printToast("Position marked in logs as "+positionMark);
		positionMark++;
	}
	
	public void toggle(View view)
	{
		if(tbutton.isChecked())
		{
			printToast("Data aquisition started");
		}
		else
		{
			printToast("Data aquisition paused");
		}
	}
	
	public void printToast(String string)
	{
		Context context = getApplicationContext();
		CharSequence text = string;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	public void saveLogsToFile() throws IOException
	{
		File externalStorageDir = Environment.getExternalStorageDirectory();
		File playNumbersDir = new File(externalStorageDir, "Sensors");
		String filename = String.format("%02d-%02d.txt", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
		File myFile = new File(playNumbersDir, filename);

		if (!playNumbersDir.exists()) {
		    playNumbersDir.mkdirs();
		}
		if(!myFile.exists()){
		    myFile.createNewFile();
		}
		
		FileOutputStream fOut = new FileOutputStream(myFile);
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		for (int index=0; index<Logs.size(); ++index)
		{
			myOutWriter.write(Logs.get(index) + "\n");
		}
	    printToast(filename);
		myOutWriter.close();
		fOut.close();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        timer = new MyTimer();
        timer.sendEmptyMessage(MyTimer.TIMER_1);
        calendar = Calendar.getInstance();
        
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
        
        labelGx1 = (TextView) findViewById(R.id.labelGx1);
        labelGy1 = (TextView) findViewById(R.id.labelGy1);
        labelGz1 = (TextView) findViewById(R.id.labelGz1);
        
        pbarGx = (ProgressBar) findViewById(R.id.progressBarGx);
        pbarGy = (ProgressBar) findViewById(R.id.progressBarGy);
        pbarGz = (ProgressBar) findViewById(R.id.progressBarGz);
        
        pbarGx.setMax(3);
        pbarGy.setMax(3);
        pbarGz.setMax(3);
        
        tbutton = (ToggleButton) findViewById(R.id.toggleButton1);
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
    	    	  
    	  labelx1.setText(String.format("X:%03.2f m/s",gravity[0]));
    	  labely1.setText(String.format("Y:%03.2f m/s",gravity[1]));
    	  labelz1.setText(String.format("Z:%03.2f m/s",gravity[2]));
    	  
    	  pbarx.setProgress((int)(Math.abs(gravity[0])*10.0));
    	  pbary.setProgress((int)(Math.abs(gravity[1])*10.0));
    	  pbarz.setProgress((int)(Math.abs(gravity[2])*10.0));
    	  
    	  labelRx1.setText(String.format("RX:%3.2f °",orientation[0]));
    	  labelRy1.setText(String.format("RY:%3.2f °",orientation[1]));
    	  labelRz1.setText(String.format("RZ:%3.2f °",orientation[2]));
    	  
    	  pbarRx.setProgress((int)(orientation[0]));
    	  pbarRy.setProgress((int)(orientation[1]));
    	  pbarRz.setProgress((int)(orientation[2]));
    	  
    	  labelGx1.setText(String.format("GX:%3.2f rad/s",gyroscope[0]));
    	  labelGy1.setText(String.format("GY:%3.2f rad/s",gyroscope[1]));
    	  labelGz1.setText(String.format("GZ:%3.2f rad/s",gyroscope[2]));
    	  
    	  pbarGx.setProgress((int)(gyroscope[0]));
    	  pbarGy.setProgress((int)(gyroscope[1]));
    	  pbarGz.setProgress((int)(gyroscope[2]));
    }
    
    @Override 
    protected void onResume() {
      super.onResume();
      printToast("onResume()");
      mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
      stopService(new Intent(this, MainActivity.class));
      timer.sendEmptyMessage(0);
      try {
		saveLogsToFile();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      finish();
      super.onPause();
      mSensorManager.unregisterListener(this);


    }
    
    public class MyTimer extends Handler
    {
    	public int counter = 0;
        public static final int TIMER_1 = 1000;

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TIMER_1:
                    if(tbutton.isChecked())
                    {
                    int hours = calendar.get(Calendar.HOUR);
                    int minutes = calendar.get(Calendar.MINUTE);
                    int seconds = calendar.get(Calendar.SECOND);
                    int milis = calendar.get(Calendar.MILLISECOND);
                    
                    Logs.add(String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milis)+",X:0.00,Y:0.00,Z:0.00");
                    counter++;
                    editText.setText("Logs has " + Logs.size() + " items");
                    }
                    sendEmptyMessageDelayed(TIMER_1, 100);
                    break;
                default:
                    removeMessages(TIMER_1);
                    break;
            }
        }
    }
}
