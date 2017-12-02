package cs175.alphafitness;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

public class RecordWorkout extends AppCompatActivity implements OnMapReadyCallback {
    //Map's variable
    private GoogleMap mMap = null;
    private Location mLocation = null;
    private ArrayList<LatLng> points;
    private double distance;

    Boolean record;
    private double mdistance;
    int status = 0;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection = null;

    PortraitFragment portraitFragment;
    private static final String TAG = "PortraitFragment";
    private String URL1;
    private Uri profile;
    private String URL2;
    private Uri workouts;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);
        contentValues = new ContentValues();
        URL1 = "content://cs175.alphafitness/profile";
        profile = Uri.parse(URL1);
        URL2 = "content://cs175.alphafitness/workout";
        workouts = Uri.parse(URL2);
        //Dummy data for profile
        Cursor cursor = managedQuery(profile, null, null, null, "user_id");
        String existName = "";
        int count = cursor.getCount();
        if(count != 0) {
            if(cursor.moveToFirst()){
                do {
                    existName = cursor.getString(cursor.getColumnIndex(MyContentProvider.KEY_NAME));
                } while (cursor.moveToNext());
            }
        }
        String name = "Tester";
        if(name.equals(existName)){
            getContentResolver().delete(profile, MyContentProvider.KEY_NAME + "=?", new String[]{existName});
        }
        contentValues.put(MyContentProvider.KEY_NAME, "Tester");
        contentValues.put(MyContentProvider.KEY_GENDER, "Female");
        contentValues.put(MyContentProvider.KEY_WEIGHT, 100.0);
        getContentResolver().insert(MyContentProvider.URI1, contentValues);
        contentValues.clear();
        //uncomment to get dummy data
        getDummyData();

        record = false;

        portraitFragment = (PortraitFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        if (portraitFragment == null) {
            portraitFragment = new PortraitFragment();
            getFragmentManager().beginTransaction().add(portraitFragment, TAG);
            portraitFragment.getmCounterStepsSteps();
            portraitFragment.startWorkout();
        }

        //Initialize the sericei
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("cs175.alphafitness", cs175.alphafitness.MyService.class.getName());
        if (!bindService(intent, remoteConnection, BIND_AUTO_CREATE)) {
            Toast.makeText(this, "Fail to bind the remote service ", Toast.LENGTH_LONG).show();
        }

        //Initialize map
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        transaction.show(fragment);
        transaction.commit();
        fragment.getMapAsync(this);

        points = new ArrayList<LatLng>();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double newLatitude = 0;
                double newLongtitude = 0;
                newLatitude = location.getLatitude();
                newLongtitude = location.getLongitude();
                PolylineOptions options = new PolylineOptions();
                LatLng here = new LatLng(newLatitude, newLongtitude);
                options.add(here);
                
                if(mMap != null){
                    if(mLocation == null){
                        mLocation = location;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 15));
                    }else {
                        options.add(here);
                        if(options != null) {
                            mMap.addPolyline(options.width(10).color(Color.BLUE));
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 15));

                    }
                }

            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    public void getDummyData(){
        for(int i =0; i < 30; i++){
            double d = (double) (Math.random()*(2.0 - 0.1) + 0.1);
            double cal = d*1.6*100;     // estimated walk 1 mile burn 100 cal
            String t = Long.toString((new Double(d*1080000.0)).longValue()); // average walk 18 min/mile
            Date da = new Date();
            contentValues.put(MyContentProvider.KEY_DISTANCE, d);
            contentValues.put(MyContentProvider.KEY_WORKOUTS, 1);
            contentValues.put(MyContentProvider.KEY_CALO, cal);
            contentValues.put(MyContentProvider.KEY_TIME, t);
            contentValues.put(MyContentProvider.DATE, da.toString());
            getContentResolver().insert(MyContentProvider.URI2, contentValues);
            contentValues.clear();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    class RemoteConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IMyAidlInterface.Stub.asInterface((IBinder) service);
            Toast.makeText(RecordWorkout.this, "Remote Service connected.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
            Toast.makeText(RecordWorkout.this, "Remote Service Disconnected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(remoteConnection);
        remoteConnection = null;
    }

    public void updateWorkout(){
        try{
            portraitFragment.startWorkout();
            portraitFragment.getmCounterStepsSteps();
            throw new RemoteException("no Remote Service found");
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

}
