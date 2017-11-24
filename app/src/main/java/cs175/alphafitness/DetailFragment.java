package cs175.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Loan Vo on 11/14/17.
 */

public class DetailFragment extends Fragment{
    Timer timer;
    LinkedList<Integer> linkedList;
    PortraitFragment portraitFragment;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        linkedList = new LinkedList<>();
        portraitFragment = new PortraitFragment();
        linkedList = portraitFragment.getmCounterStepsSteps();
//        portraitFragment.startWorkout();
        /*timer = new Timer();
        timer.schedule(new TimerTask() {
            int current = 0;
            @Override
            public void run() {
                if(linkedList.size()<= 60) {
                    linkedList.addLast(current);
                }else{
                    linkedList.removeFirst();
                    linkedList.addLast(current);
                }
            }
        }, 0,5000);*/
        return view;
    }



}
