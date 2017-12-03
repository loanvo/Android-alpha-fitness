package cs175.alphafitness;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by loan vo on 11/21/17.
 */

public class PortraitFragment extends Fragment implements SensorEventListener{

    private long time;

    private Date startDate;
    private Date endDate;
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
    private Boolean record = false;

    //user profile
    //Profile profile;
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
    ContentValues contentValues;

    Handler handler;
    Timer timer;
    int current = 0;
    private int rawSteps = 0;
    private String URL1;
    private Uri profile;
    private String URL2;
    private Uri workouts;
    int userid =0;
    int status = 0;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection =null;

    private LinkedList<Integer> linkedList;
    int [] stepsArray;
    int nsteps=0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.portrait_fragment, container, false);
        setRetainInstance(true);

        linkedList = new LinkedList<Integer>();
        for(int i =0; i<60; i++){
            linkedList.add(0);
        }
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);
        URL2 = "content://cs175.alphafitness/workout";
        workouts = Uri.parse(URL2);
        Cursor cursor = getActivity().managedQuery(profile, null, null, null, "user_id");
        if(cursor.moveToFirst()) {
            weight = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_WEIGHT)));
        }
            contentValues = new ContentValues();
            handler = new Handler();
            distanceView = (TextView) view.findViewById(R.id.distance_view);
            durationView = (TextView) view.findViewById(R.id.duration_view);


            //Initialize sensor
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            // Get user id from profile table
            Cursor pc = getActivity().managedQuery(profile, null, null, null, "user_id");
            if (pc.moveToFirst()) {
                userid = Integer.parseInt(pc.getString(pc.getColumnIndex(MyContentProvider.KEY_ID)));
            }
            // implement action for workout button
            button = (Button) view.findViewById(R.id.start_button);
            button.setTag(1);
            button.setText("Start Workout");
            button.setBackgroundColor(Color.GREEN);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int status = (Integer) v.getTag();
                    if (record==false) {
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

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                linkedList.removeFirst();
                linkedList.addLast(mSteps);

            }
        }, 0, 5000);
            return view;
    }

    public void startWorkout(){
        startDate = new Date();
        start = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        contentValues.put(MyContentProvider.USER_ID, userid);
        contentValues.put(MyContentProvider.DATE, startDate.toString());

        contentValues.put(MyContentProvider.STATUS, 1);
        contentValues.put(MyContentProvider.KEY_STEPS, rawSteps);
        getActivity().getContentResolver().insert(MyContentProvider.URI2, contentValues);
    }

    public void stopWorkout(){
        timeBuff += time;
        handler.removeCallbacks(runnable);

        endDate = new Date();
        burned_calo = calculateCaloBurned(mSteps);

        contentValues.put(MyContentProvider.USER_ID, userid);
        contentValues.put(MyContentProvider.DATE, endDate.toString());
        contentValues.put(MyContentProvider.KEY_WORKOUTS, 1);
        contentValues.put(MyContentProvider.KEY_DISTANCE,  distanceView.getText().toString());
        contentValues.put(MyContentProvider.KEY_TIME, Long.toString(timeBuff));
        contentValues.put(MyContentProvider.STATUS, 0);
        contentValues.put(MyContentProvider.KEY_CALO, burned_calo);
        getActivity().getContentResolver().insert(MyContentProvider.URI2, contentValues);

    }
    public Double getWeight(){
        return weight;
    }
    public Double calculateCaloBurned(int steps){
        Double calo;
        calo_per_mile = CONST * getWeight();
        calo_per_step = calo_per_mile / STEPS_PER_MILE;
        calo = steps * calo_per_step;
        return calo;
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
        // only register the listener if start button has not been click
        if(record==true) {
            button.setText("Stop Workout");
            button.setBackgroundColor(Color.RED);
            button.setTag(0);
        }
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(countSensor != null){
                sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(getActivity(), "Sensor not found", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onPause() {
        super.onPause();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(record==false){
            button.setText("Start Workout");
            button.setBackgroundColor(Color.GREEN);
            button.setTag(1);
            if(countSensor != null) {
                sensorManager.unregisterListener(this,countSensor);
            }else
                Toast.makeText(getActivity(), "Sensor not found", Toast.LENGTH_SHORT).show();
            }else{
            sensorManager.registerListener(this,countSensor, sensorManager.SENSOR_DELAY_FASTEST);

        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (record == true) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                rawSteps = (int) event.values[0];
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

    public LinkedList<Integer> getmCounterStepsSteps(){
        Log.d("port========", linkedList.toString());


        return linkedList;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
