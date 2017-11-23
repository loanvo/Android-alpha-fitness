package cs175.alphafitness;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
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

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by mach on 11/22/17.
 */
public class ButtonFragment extends Fragment{
    private long time;

    private DateTime startDate;
    private DateTime endDate;
    //variable for time counter
    private long start, end, timeBuff, updateTime = 0L;
    private int hours, seconds, minutes;

    private TextView distanceView;
    private TextView durationView;
    private Button button;

    Boolean record;
    WorkoutData data;
    //format distance data type
    DecimalFormat df = new DecimalFormat("####.###");
    Handler handler;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection =null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.button_fragment, container, false);

        handler = new Handler();
       // distanceView = (TextView) view.findViewById(R.id.distance_view);
        //durationView = (TextView) view.findViewById(R.id.duration_view);
        record = false;
        data = new WorkoutData();


        // implement action for workout button
        button = (Button) view.findViewById(R.id.start_button);
        button.setTag(1);
        button.setText("Start Workout");
        button.setBackgroundColor(Color.GREEN);
        button.setOnClickListener(new View.OnClickListener(){
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
          //  durationView.setText("" +  String.format("%02d", hours) + ":"
            //        + String.format("%02d", minutes) + ":"
              //      + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }
    };


}
