package com.quotion;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {


    int notificationId = 0;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static int value;
    private static int j;

    private static String[] items;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference reference_1 = database.getReference("Users/" + LoginState.uID + "/value");

        reference_1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(Integer.class);

                if (!(value == 0)) {

                    items = new String[value];

                    DatabaseReference reference_2;

                    for (int i = 0; i < value; i++) {

                        j = i;

                        reference_2 = database.getReference("Users/" + LoginState.uID + "/quotes/" + i);
                        reference_2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String quote = dataSnapshot.getValue(String.class);

                                System.out.println("key: " + dataSnapshot.getKey());

                                items[Integer.parseInt(dataSnapshot.getKey())] = quote;

                                System.out.println("items: " + Arrays.toString(items));

                                if (value - 1 == Integer.parseInt(dataSnapshot.getKey())) {

                                    int rnd = new Random().nextInt(value);
                                    String Quote = items[rnd];

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "123")
                                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                                            .setContentTitle("Quotion")
                                            .setContentText(Quote)
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(Quote))
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    // notificationId is a unique int for each notification that you must define
                                    notificationManager.notify(notificationId, builder.build());
                                    notificationId++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w("Failed to read value.", error.toException());
                            }
                        });
                    }
                    System.out.println("items:" + Arrays.toString(items));

                } else {
                    System.out.println("No Quotes");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });
    }

}
