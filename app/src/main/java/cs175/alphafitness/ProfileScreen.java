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
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        mprofile = new Profile();
        contentValues = new ContentValues();
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);
 //       URL2 = "content://cs175.alphafitness/workout";
  //      workouts = Uri.parse(URL2);
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
    }

    public void updateProfile(){

    }

    @Override
    public void onClick(View v) {
        setContentView(R.layout.new_profile_layout);
        nameEdit = (EditText) findViewById(R.id.edit_name);
        genderEdit = (EditText) findViewById(R.id.edit_gender);
        weightEdit = (EditText) findViewById(R.id.edit_weight);
        saveButton = (Button) findViewById(R.id.save_button);
        Cursor cursor = managedQuery(profile, null, null, null, "user_id");
        String n="";
        String g = "";
        String w = "";
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
