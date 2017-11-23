package cs175.alphafitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    Profile profile;
    private String name;
    private String gender;
    private Double weight = 0.0;
    ContentValues contentValues = new ContentValues();

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

        Intent intent = getIntent();
        String userName;
        userName = intent.getStringExtra("username");
        nameView.setText(userName);
        String userGender;
        userGender = intent.getStringExtra("gender");
        genderView.setText(gender);
        Double userWeight;
        userWeight = Double.parseDouble(intent.getStringExtra("weight"));
        genderView.setText(gender);

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
        nameEdit = (EditText) findViewById(R.id.edit_name);
        name = nameEdit.getText().toString();

        genderEdit = (EditText) findViewById(R.id.edit_gender);
        gender = genderEdit.getText().toString();

        weightEdit = (EditText) findViewById(R.id.edit_weight);
        weight = Double.parseDouble(weightEdit.getText().toString());

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile = new Profile();
                profile.setName(name);
                profile.setGender(gender);
                profile.setWeight(weight);

                contentValues.put(MyContentProvider.KEY_NAME, name);
                contentValues.put(MyContentProvider.KEY_GENDER, gender);
                contentValues.put(MyContentProvider.KEY_WEIGHT, weight);

                Uri uri = getContentResolver().insert(MyContentProvider.URI1, contentValues);
                Toast.makeText(v.getContext(), uri.toString(), Toast.LENGTH_LONG).show();

                if(nameExist(name)){

                }
                Intent updateInfo = new Intent(v.getContext(), ProfileScreen.class);
                updateInfo.putExtra("username", name);
                updateInfo.putExtra("gender", gender);
                //updateInfo.putExtra("weight", weight);
                startActivity(updateInfo);
            }

            public Boolean nameExist(String name){

                return false;
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_profile_screen);
            }
        });



    }
}
