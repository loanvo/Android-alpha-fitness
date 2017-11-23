package cs175.alphafitness;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
    IMyAidlInterface.Stub mBinder;
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new IMyAidlInterface.Stub(){
            public void updateWorkout(){}
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
}
