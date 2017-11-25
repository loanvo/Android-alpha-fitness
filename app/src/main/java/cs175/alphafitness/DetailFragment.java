package cs175.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Loan Vo on 11/14/17.
 */

public class DetailFragment extends Fragment{
    Timer timer;
    LinkedList<Integer> linkedList;
    ArrayList<Double> caloBurnList;
    PortraitFragment portraitFragment;
    private LineChart lineChart;
    String URL1;
    Uri profile;
    Double weight =0.0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        linkedList = new LinkedList<>();
        caloBurnList = new ArrayList<>();
        portraitFragment = (PortraitFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        linkedList = portraitFragment.getmCounterStepsSteps();

        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);
        /*Cursor cursor = getActivity().managedQuery(profile, null, null, null, "user_id");

        if(cursor.moveToFirst()) {
            weight = Double.parseDouble(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_WEIGHT)));
        }*/
        int steps = 0;
        Double caloBurned =0.0;
        for(int i =0; i<linkedList.size()-1; i++){
            steps = linkedList.get(i+1) - linkedList.get(i);
            caloBurned = portraitFragment.calculateCaloBurned(steps);
            caloBurnList.add(caloBurned);
        }
        lineChart = (LineChart) view.findViewById(R.id.linechart);
/*
        setData();
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        //lineChart.setOnChartGestureListener();
       // lineChart.setOnChartValueSelectedListener();

     //        portraitFragment.startWorkout();
  /*    comment out
   timer = new Timer();

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
        }, 0,5000); ---------
        return view;
    }
    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i =0; i < linkedList.size(); i++){
           // yVals.add(new Entry((float) i , i));
            yVals.add(new Entry((float) linkedList.get(i) , i));
        }
        yVals.add(new Entry(60, 0));
        yVals.add(new Entry(48, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));
        return yVals;
    }

    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<>();
            for(int i = 0; i< linkedList.size(); i = ++){
            xVals.add(Integer.toString(linkedList.get(i)));
        }
        return xVals;
    }

    private void setData(){
//        ArrayList<String> xVals = setXAxisValues();
        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals, "Calo Burned");
            set1.setFillColor(Color.LTGRAY);
        }
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        // set data
        lineChart.setData(data);*/

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        //setData(100, 30);
   /*     for(int i = 0; i< caloBurnList.size(); i++) {
            setData(i, Double.valueOf(caloBurnList.get(i)).floatValue());
            lineChart.invalidate();
        }*/
   //testing
        for(int i = 0; i< caloBurnList.size(); i++) {
            setData(i, Double.valueOf(10*i).floatValue());
            lineChart.invalidate();
        }
        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
       // xAxis.setGranularity(1f); // one hour


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        return view;
    }





    private void setData(int count, float range) {

        // now in hours
        //long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();
       // for(int i =0; i <= count; i++){
            // yVals.add(new Entry((float) i , i));
            //values.add(new Entry(i*5 , Double.valueOf(caloBurnList.get(i)).floatValue()));
            values.add(new Entry(count*5 ,range));

       // }
    /*

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        Random r = new Random();
        float min = 0.0f;
        float max = 50.0f;
        for (float x = from; x < to; x++) {

            float y = (float) r.nextFloat()*(max - min) + min;
            values.add(new Entry(x, y)); // add one entry per hour
        }
*/
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Calo Burned");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        lineChart.setData(data);
    }




}
