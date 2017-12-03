package cs175.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    ArrayList<Double> distanceList;
    PortraitFragment portraitFragment;

    private LineChart lineChart;
    private int fillColor = Color.argb(150, 51, 181, 229);

    String URL1;
    Uri profile;
    Double weight =0.0;

    DecimalFormat df = new DecimalFormat("####.##");

    TextView averageView;
    TextView minView;
    TextView maxView;

    String average = "";  // (min/km)
    int  minCount = 0;     // min
    ArrayList<Double> avgList;

    private Thread thread;
    //Sensor's variable
    SensorManager sensorManager;
    Sensor mStepCounter;
    SensorEventListener sensorEventListener;
    boolean plotData = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

       averageView = view.findViewById(R.id.avg_view);
        minView = view.findViewById(R.id.min_view);
        maxView = view.findViewById(R.id.max_view);
        avgList = new ArrayList<>();
        linkedList = new LinkedList<>();
        caloBurnList = new ArrayList<>();
        distanceList = new ArrayList<>();
        portraitFragment = (PortraitFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        if(portraitFragment.getmCounterStepsSteps() != null) {
            linkedList = portraitFragment.getmCounterStepsSteps();
        }
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);

        int steps = 0;
        double distance = 0.0;
        Double caloBurned = 0.0;
        for (int i = 0; i < linkedList.size() - 1; i++) {
            steps = linkedList.get(i);
            caloBurned = portraitFragment.calculateCaloBurned(steps);
            if(caloBurned == 0){
                caloBurnList.add(0.0);
            }else {
                caloBurnList.add(caloBurned);
            }
            distance = steps * portraitFragment.STEP_LENGTH / 1000.0;
            distanceList.add(distance);


        }
        double total = 0.0;
        for(int j =0; j < distanceList.size(); j++){
            total += distanceList.get(j);
        }
        average = df.format(total /distanceList.size());

        averageView.setText(average);
        minView.setText(df.format((Collections.min(distanceList))));
        maxView.setText(df.format(Collections.max(distanceList)));

        // Draw line chart of burned calo in every 5 min
        lineChart = (LineChart) view.findViewById(R.id.linechart);
        lineChart.setDrawGridBackground(true);
       //lineChart.setDrawGridLine

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);

        XAxis xl = lineChart.getXAxis();
        xl.setAxisMinimum(0f);
        xl.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        setDara(distanceList, caloBurnList);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        lineChart.invalidate();
        return view;
    }


    public void setDara(ArrayList<Double> distances,ArrayList<Double> ranges){
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < ranges.size(); i++) {
            float xVal = (float) i;
            float yVal = Float.parseFloat(String.valueOf(ranges.get(i)) );
            entries1.add(new Entry(xVal, yVal));
        }
        ArrayList<ILineDataSet> dataset= new ArrayList<>();
       // Collections.sort(entries, new EntryXComparator());
        LineDataSet set1 = new LineDataSet(entries1, "Calo Burned in 5 min");
        set1.setDrawCircles(false);
        set1.setFillColor(Color.MAGENTA);
        set1.setLineWidth(3f);
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setFillColor(Color.CYAN);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        dataset.add(set1);

        ArrayList<Entry> entries2 = new ArrayList<Entry>();
        for (int i = 0; i < distances.size(); i++) {
            float xVal = (float) i;
            float yVal = Float.parseFloat(String.valueOf(distances.get(i)) );
            entries2.add(new Entry(xVal, yVal));
        }
        LineDataSet set2 = new LineDataSet(entries1, "Calo Burned in 5 min");
        set2.setDrawCircles(false);
        set2.setFillColor(Color.GRAY);
        set2.setLineWidth(3f);
        set2.setFillAlpha(255);
        set2.setDrawFilled(true);
        set2.setFillColor(Color.LTGRAY);
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set2.setCubicIntensity(0.2f);
        dataset.add(set2);
        LineData data = new LineData(dataset);

        lineChart.setData(data);
    }

}
