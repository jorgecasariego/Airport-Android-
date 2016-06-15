package casariego.jorge.airport;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

/**
 * Created by jorgecasariego on 15/6/16.
 *
 *  What is ranging?
 *  While monitoring creates a virtual fence to detect when you’re moving in and out,
 *  ranging actively scans for any nearby beacons and delivers results to you every second.
 *
 *  Proximity estimation
 *  Each beacon broadcasts its Bluetooth signal with a certain strength—a strength which diminishes
 *  as the signal travels through the air. This enables the receiver device to make a rough
 *  estimation of how far the beacon is. Strong signal = it’s close. Weak signal = it’s further away.
 *
 *  Ranging provides more granular and comprehensive beacon data, but this comes at the expense of
 *  draining the battery faster than monitoring. This means that it’s usually not a good idea to
 *  run ranging for extended periods of time, e.g., hours. It certainly wouldn’t be viable to run
 *  it at all times.
 */
public class MyApplication extends Application {
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());

        // 1. let’s create a beacon region defining our monitoring geofence,
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        64160, 33963));
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification(
                        "Your gate closes in 47 minutes.",
                        "Current security wait time is 15 minutes, "
                                + "and it's a 5 minute walk from security to the gate. "
                                + "Looks like you've got plenty of time!");
            }

            @Override
            public void onExitedRegion(Region region) {
                showNotification(
                        "Hasta luego",
                        "Le esperamos pronto!");
            }
        });
    }

    public void showNotification(String title, String message){
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);;


    }
}
