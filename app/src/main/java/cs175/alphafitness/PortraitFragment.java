package cs175.alphafitness;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.LinkedList;

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
    private int rawSteps;
    int tracker = 0;
    private Boolean record;

    //user profile
    Profile profile;
    private int userID;
    private double weight;
    private Button profileButton;

    //Casual walking 2mph
    static final double CONST = 0.57;
    static final double STEPS_PER_MILE = 2200;
    private double calo_per_mile;
    private double calo_per_step;
    private double burned_calo;

    //format distance data type
    DecimalFormat df = new DecimalFormat("####.###");
    ContentValues contentValues = new ContentValues();

    Handler handler;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection =null;

    private LinkedList<Integer> linkedList;
    long start_timer = 0L;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.portrait_fragment, container, false);

        handler = new Handler();
        distanceView = (TextView) view.findViewById(R.id.distance_view);
        durationView = (TextView) view.findViewById(R.id.duration_view);

        record = false;
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

        // user profile
        profileButton = (Button) view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileScreen.class);
                startActivity(intent);
            }
        });


        return view;
    }

    public void startWorkout(){
        startDate = new DateTime();
        start = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

       // contentValues.put(MyContentProvider.USER_ID, userID);
        contentValues.put(MyContentProvider.DATE, startDate.toString());
        contentValues.put(MyContentProvider.KEY_WORKOUTS, 1);
        getActivity().getContentResolver().insert(MyContentProvider.URI2, contentValues);
    }

    public void stopWorkout(){
        timeBuff += time;
        handler.removeCallbacks(runnable);

        endDate = new DateTime();

        //calculate burned calories
        calo_per_mile = CONST * weight;
        calo_per_step = calo_per_mile / STEPS_PER_MILE;
        burned_calo = mSteps * calo_per_step;

        contentValues.put(MyContentProvider.KEY_TIME, timeBuff);
        contentValues.put(MyContentProvider.KEY_CALO, burned_calo);
        getActivity().getContentResolver().insert(MyContentProvider.URI2, contentValues);
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
        linkedList = new LinkedList<Integer>();
        if (record == true) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                contentValues.put(MyContentProvider.KEY_STEPS, (int) event.values[0]);
                contentValues.put(MyContentProvider.DATE, new DateTime().toString());
                getActivity().getContentResolver().insert(MyContentProvider.URI2, contentValues);

                start_timer = timeBuff - start_timer;

                if(start_timer == 2000) {
                    if(linkedList.size()<= 60) {
                        linkedList.add((int) event.values[0]);
                    }else{
                        linkedList.removeFirst();
                        linkedList.addLast((int) event.values[0]);
                    }
                }
                Log.e("list===========", linkedList.toString());
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
