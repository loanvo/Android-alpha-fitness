package cs175.alphafitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener{
    private TextView nameView;
    private TextView genderView;
    private TextView weightView;

    //user info layout
    private Button saveButton;
    private Button cancelButton;
    private EditText nameEdit;
    private EditText genderEdit;
    private EditText weightEdit;

    Profile mprofile;
    private String name;
    private String gender;
    private Double weight = 0.0;
    private ContentValues contentValues;
    private String URL1;
    private Uri profile;
    private String URL2;
    private Uri workouts;

    private TextView week_distance_view;
    private TextView week_time_view;
    private TextView week_workouts_view;
    private TextView week_calo_view;
    private TextView distance_view;
    private TextView time_view;
    private TextView workouts_view;
    private TextView calo_view;

    Double distance = 0.0;
    long time = 0L;
    int workout = 0;
    Double calo = 0.0;
    List<Date> dateList;
    Double weekly_distance = 0.0;
    long weekly_time = 0L;
    int weekly_workout = 0;
    Double weekly_calo = 0.0;

    String selectName= "";
    int id = 0;
    private Date mdate= null;

    List<Integer> weekdayList;
    String timeView;
    DecimalFormat df = new DecimalFormat("####.###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        mprofile = new Profile();

        contentValues = new ContentValues();
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);

        dateList = new ArrayList<>();
        weekdayList = new ArrayList<>();

        nameView = (TextView) findViewById(R.id.name_view);
        genderView = (TextView) findViewById(R.id.gender_view);
        weightView = (TextView) findViewById(R.id.weight_view);

        Cursor cursor = managedQuery(profile, null, null, null, "user_id");
        if(cursor == null){
            setContentView(R.layout.new_profile_layout);
        }else if(cursor.moveToFirst()){

            nameView.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_NAME)));
            genderView.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_GENDER)));
            weightView.setText(Double.toString(cursor.getDouble(cursor.getColumnIndex(MyContentProvider.KEY_WEIGHT))));
        }
        nameView.setOnClickListener(this);
        genderView.setOnClickListener(this);
        weightView.setOnClickListener(this);

        URL2 = "content://cs175.alphafitness/workout";
        workouts = Uri.parse(URL2);
        week_distance_view = (TextView) findViewById(R.id.week_workout_distance_view);
        week_time_view = (TextView) findViewById(R.id.week_time_view);
        week_workouts_view = (TextView) findViewById(R.id.week_workout_view);
        week_calo_view = (TextView) findViewById(R.id.week_calo_view);
        distance_view = (TextView) findViewById(R.id.workout_distance_view);
        workouts_view = (TextView) findViewById(R.id.workout_view);
        time_view = (TextView) findViewById(R.id.time_view);
        calo_view = (TextView) findViewById(R.id.calo_view);

        Cursor c = managedQuery(profile, null, null, null, "user_id");
        if(c.moveToFirst()){
            selectName = c.getString(c.getColumnIndex(MyContentProvider.KEY_NAME));
            id = Integer.parseInt(c.getString(c.getColumnIndex(MyContentProvider.KEY_ID)));
            }
        Cursor curW = managedQuery(workouts, null, MyContentProvider.USER_ID + "=?", new String []{Integer.toString(id)}, "id");
        String timeString = "";
            if (cursor != null && curW.moveToFirst()) {
                do {
                    //get colunms from workouts table
                    String str1 = curW.getString(curW.getColumnIndex(MyContentProvider.KEY_DISTANCE));
                    String str2 = curW.getString(curW.getColumnIndex(MyContentProvider.KEY_TIME));
                    String str4 = curW.getString(curW.getColumnIndex(MyContentProvider.KEY_WORKOUTS));
                    String str5 = curW.getString(curW.getColumnIndex(MyContentProvider.KEY_CALO));
                    String str3 = curW.getString(curW.getColumnIndex(MyContentProvider.DATE));
                    if(str1 == null || str1.equalsIgnoreCase("null") || str1.isEmpty()){
                        distance += 0.0;
                    }else {
                        //distance += Double.parseDouble(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_DISTANCE)));
                        distance += Double.parseDouble(str1);
                    }
                    if(str2 == null || str2.equalsIgnoreCase("null" ) || str2.isEmpty()){
                        time += 0L;
                    }else {
                        timeString = curW.getString(curW.getColumnIndex(MyContentProvider.KEY_TIME));
                        time += (Time.valueOf(timeString)).getTime();
                    }

                    if(str4 == null || str4.equalsIgnoreCase("null") || str4.isEmpty()){
                        workout  += 0;
                    }else {
                        workout += Integer.parseInt(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_WORKOUTS)));
                    }
                    if(str5 == null || str5.equalsIgnoreCase("null") || str5.isEmpty()){
                        calo  += 0.0;
                    }else {
                        calo += Double.parseDouble(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_CALO)));
                    }
                    if(str3 == null || str3.equalsIgnoreCase("null") || str3.isEmpty()){
                        dateList.add(null);
                    }else {
                        dateList.add(formatDate(curW.getString(curW.getColumnIndex(MyContentProvider.DATE))));
                    }

                } while (curW.moveToNext());

                time_view.setText(formatTimeView(time));
                distance_view.setText((df.format(distance)) + " km");
                workouts_view.setText(Integer.toString(workout) + " times");
                calo_view.setText(df.format(calo) + " cal");

                // calculate avg/weekly workouts
                weekly_distance = (distance/dateList.size())*7;
                weekly_time = (time/dateList.size())*7;
                weekly_calo = (calo/dateList.size())*7;
                weekly_workout = (workout/dateList.size())*7;

                // set average weekly workouts view
                week_distance_view.setText(df.format(weekly_distance) + " km");
                week_time_view.setText(formatTimeView(weekly_time));
                week_workouts_view.setText(Integer.toString(weekly_workout) + " times");
                week_calo_view.setText(df.format(weekly_calo) + " cal");
            }
    }

    public String formatTimeView(long millis){
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes/60;
        int day = hours/24;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        timeView = "" + String.format("%02d", day) + " day "
                +  String.format("%02d", hours) + " hr "
                + String.format("%02d", minutes) + " min "
                + String.format("%02d", seconds) + " sec";
        return timeView;
    }

   public Date formatDate(String date){
       SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
       try {
            mdate = format.parse(date);
       }catch (java.text.ParseException e){
           e.printStackTrace();
       }
       return mdate;
    }

    @Override
    public void onClick(View v) {
        setContentView(R.layout.new_profile_layout);
        nameEdit = (EditText) findViewById(R.id.edit_name);
        genderEdit = (EditText) findViewById(R.id.edit_gender);
        weightEdit = (EditText) findViewById(R.id.edit_weight);
        saveButton = (Button) findViewById(R.id.save_button);
        Cursor cursor = managedQuery(profile, null, null, null, "user_id");
        if(cursor.moveToFirst()) {
            nameEdit.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_NAME)));
            genderEdit.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_GENDER)));
            weightEdit.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_WEIGHT)));
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = managedQuery(profile, null, null, null, "user_id");
                String existName = "";
                int count = cursor.getCount();
                if(count != 0) {
                    if(cursor.moveToFirst()){
                        do {
                            existName = cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_NAME));
                            getContentResolver().delete(profile, MyContentProvider.KEY_NAME + "=?", new String[]{existName});
                        } while (cursor.moveToNext());
                    }
                }
                name = nameEdit.getText().toString();
                gender = genderEdit.getText().toString();
                weight = Double.parseDouble(weightEdit.getText().toString());

                contentValues.put(MyContentProvider.KEY_NAME, name);
                contentValues.put(MyContentProvider.KEY_GENDER, gender);
                contentValues.put(MyContentProvider.KEY_WEIGHT, weight);
                getContentResolver().insert(MyContentProvider.URI1, contentValues);

                Cursor c = managedQuery(profile, null, null, null, "user_id");
                int userid = 0;
                if(c.moveToFirst()){
                    selectName = c.getString(c.getColumnIndex(MyContentProvider.KEY_NAME));
                    userid = Integer.parseInt(c.getString(c.getColumnIndex(MyContentProvider.KEY_ID)));
                }
                //insert userID into workouts table
                contentValues.put(MyContentProvider.USER_ID, userid);
                getContentResolver().insert(MyContentProvider.URI2, contentValues);

                setContentView(R.layout.activity_profile_screen);
                nameView = (TextView) findViewById(R.id.name_view);
                genderView = (TextView) findViewById(R.id.gender_view);
                weightView = (TextView) findViewById(R.id.weight_view);
                nameView.setText(name);
                genderView.setText(gender);
                weightView.setText(Double.toString(weight));
                Intent intent = new Intent(v.getContext(), ProfileScreen.class);
                startActivity(intent);
                finish();
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
