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

import org.joda.time.DateTime;

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


//    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        mprofile = new Profile();
        contentValues = new ContentValues();
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);
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

        Double distance = 0.0;
        long time = 0L;
        int workout = 0;
        DateTime date = null;
        Double calo = 0.0;

        Cursor curW = managedQuery(workouts, null, null, null, "id");
        if(curW.moveToFirst()){
            do {
                distance += Double.parseDouble(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_DISTANCE)));
                time += Long.parseLong(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_TIME)));
                workout += Integer.parseInt(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_WORKOUTS)));
                calo += Double.parseDouble(curW.getString(curW.getColumnIndex(MyContentProvider.KEY_CALO)));
            } while(curW.moveToNext());

            distance_view.setText(Double.toString(distance));
            time_view.setText(Long.toString(time));
            calo_view.setText(Double.toString(calo));
        }



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
