package com.example.walkbuddy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;


public class StepsService extends Service implements SensorEventListener {

    //It is a foreground service which runs even when the app is killed
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    SensorManager sensorManager;
    Sensor stepDetector;
    Boolean sensorIsPresent;
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mfirebaseUser;
    private String date;
    private Double height, weight;
    Notification notification;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
            stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorIsPresent = true;
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            sensorIsPresent = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        if(Build.VERSION.SDK_INT >= 26) {

            Intent stopIntent = new Intent(this, StopReceiver.class);
            stopIntent.setAction("com.WalkBuddy.STOP_INTENT");
            PendingIntent stopPendingIntent =
                    PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);


            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("WalkBuddy")
                    .setContentText("Tracking steps...")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.service_stop, "Stop",
                            stopPendingIntent)
                    .build();
        } else {
            //for API level <26 there no option is implemented to stop service via notification
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("WalkBuddy")
                    .setContentText("Tracking steps...")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(1, notification);

        mfirebaseAuth = FirebaseAuth.getInstance();
        mfirebaseUser = mfirebaseAuth.getCurrentUser();

        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(id).child("info");
        ref1.get().addOnSuccessListener(dataSnapshot -> {
            height = Double.parseDouble(dataSnapshot.child("height").getValue().toString());
            weight = Double.parseDouble(dataSnapshot.child("weight").getValue().toString());
        });
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this, stepDetector);
//        stopForeground(true);
//        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        date = Utility.zeroX(day) + "," + Utility.zeroX((month+1)) + "," + Utility.zeroX(year);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if(!sharedPreferences.contains("today")){
            myEdit.putString("today", date);
            myEdit.putInt("steps",0);
            myEdit.apply();
        } else {
            if(sharedPreferences.getString("today","0").equals(date)){
                int steps = sharedPreferences.getInt("steps",0);
                steps++;
                myEdit.putInt("steps",steps);
                myEdit.apply();
                incrementSteps(steps);
            } else {
                myEdit.clear().apply();
                myEdit.putString("today", date);
                myEdit.putInt("steps",0);
                myEdit.apply();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void incrementSteps(int s){

        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        ref.get().addOnSuccessListener(dataSnapshot -> {
            if(!dataSnapshot.hasChild(date)){
                ref.child(date).child("s").setValue(0);
                ref.child(date).child("c").setValue(0);
                ref.child(date).child("d").setValue(0);
            }
            ref.child(date).child("s").setValue(s);
            setdiscal(s);
        });
    }

    private void setdiscal(int steps){
        String str = mfirebaseUser.getEmail();
        String id = Utility.EncodeString(Objects.requireNonNull(str));
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(id).child("steps");
        double h = height/2.54; // convert height in cm to inches
        h = Math.round(h * 100.0) / 100.0;
        double s = h * 0.42; // stride length (inches) approx = height (in inches) * 0.42
        double sc = s * 2.54; // convert stride length from inches to cm
        sc = Math.round(sc * 100.0) / 100.0;
        double dc = steps * sc; // distance travelled (in cms) = steps * stride(cms)
        dc = Math.round(dc * 100.0) / 100.0;
        double dk = dc / 100000; // convert distance in cm to km
        dk = Math.round(dk * 1000.0) / 1000.0;
        ref1.child(date).child("d").setValue(dk);

        double dm = dk / 1.609; // convert distance in km to miles
        dm = Math.round(dm * 1000.0) / 1000.0;
        double wp = weight * 2.205;// convert weight in kgs to pounds
        double calpermile = wp * 0.52; // according to American council on exercise
        // 0.52 calories per pound per mile is burned if individual walks at brisk pace i.e 3 mph
        double cal = dm*calpermile;
        cal = Math.round(cal * 10.0) / 10.0;
        ref1.child(date).child("c").setValue(cal);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
