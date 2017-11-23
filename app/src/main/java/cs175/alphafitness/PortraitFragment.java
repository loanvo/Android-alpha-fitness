package cs175.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telecom.RemoteConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

/**
 * Created by loan vo on 11/21/17.
 */

public class PortraitFragment extends Fragment implements SensorEventListener{

    private long time;

    private DateTime startDate;
    private DateTime endDate;
    //variable for time counter
    private long start, end, timeBuff, updateTime = 0L;
    private int hours, seconds, minutes;

    private TextView distanceView;
    private TextView durationView;
    private Button button;

    //Sensor's variable
    SensorManager sensorManager;
    Sensor mStepCounter;
    private int mSteps = 0;
    private int mCounterSteps = 0;
    final static double STEP_LENGTH = (0.67 + 0.762)/2; // average step length in meter
    private double workoutDistance;
    private Boolean record;
    WorkoutData data;
    //format distance data type
    DecimalFormat df = new DecimalFormat("####.###");
    Handler handler;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection =null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portrait_fragment, container, false);

        handler = new Handler();
         distanceView = (TextView) view.findViewById(R.id.distance_view);
        durationView = (TextView) view.findViewById(R.id.duration_view);
        record = false;
        data = new WorkoutData();

        //Initialize sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        // implement action for workout button
        button = (Button) view.findViewById(R.id.start_button);
        button.setTag(1);
        button.setText("Start Workout");
        button.setBackgroundColor(Color.GREEN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    record = true;
                    startWorkout();
                    button.setText("Stop Workout");
                    button.setBackgroundColor(Color.RED);
                    button.setTag(0);

                } else {
                    record = false;
                    stopWorkout();
                    button.setText("Start Workout");
                    button.setBackgroundColor(Color.GREEN);
                    button.setTag(1);
                }

            }
        });

        return view;
    }

    public WorkoutData startWorkout(){
        startDate = new DateTime();
        start = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        return data;
    }

    public WorkoutData stopWorkout(){
        timeBuff += time;
        handler.removeCallbacks(runnable);

        endDate = new DateTime();
        return data;
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            updateTime = timeBuff + time;

            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            hours = minutes/60;
            seconds = seconds % 60;
            time = (int) (updateTime % 1000);
              durationView.setText("" +  String.format("%02d", hours) + ":"
                    + String.format("%02d", minutes) + ":"
                  + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getActivity(), "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    //Sensor change detector
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (record == true) {

            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }
                // Calculate steps taken based on first counter value received.
                mSteps = (int) event.values[0] - mCounterSteps;

                // calculate distance base on amounts of steps in km
                workoutDistance = mSteps * STEP_LENGTH/1000;
                distanceView.setText(df.format(workoutDistance));
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
