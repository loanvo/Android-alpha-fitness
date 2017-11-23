package cs175.alphafitness;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener{
    private TextView nameView;
    private TextView genderView;
    private TextView weightView;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        nameView = (TextView) findViewById(R.id.name_view);
        nameView.setOnClickListener(this);

        genderView = (TextView) findViewById(R.id.gender_view);
        genderView.setOnClickListener(this);

        weightView = (TextView) findViewById(R.id.weight_view);
        weightView.setOnClickListener(this);



        String URL1 = "content://cs175.alphafitness/profile";
        Uri profile = Uri.parse(URL1);
        String URL2 = "content://cs175.alphafitness/workout";
        Uri workouts = Uri.parse(URL2);
        Cursor cursor = managedQuery(profile, null, null, null, "user_id");

        if(cursor.moveToFirst()){
            nameView.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_NAME)));
            //genderView.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_GENDER)));
            weightView.setText(Double.toString(cursor.getDouble(cursor.getColumnIndex(MyContentProvider.KEY_WEIGHT))));
        }
    }

    @Override
    public void onClick(View v) {
        setContentView(R.layout.new_profile_layout);
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.layout_helper);

            }
        });


    }
}
