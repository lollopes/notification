package com.example.lollo.led_test_2;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{

    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView status;
    public boolean notify_request = false;
    String value;
    String CHANNEL_ID = "my_channel_01";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button notification_button = findViewById(R.id.create_notification);
        writetoDataBase();
        readFromDataBase();
        createNotificationChannel();

        notification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                notify_request = true;
                Toast toast=Toast.makeText(getApplicationContext(),"Notification is active",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    public void addNotification()
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 1;
        Notification notification = new Notification.Builder(MainActivity.this)
                .setContentTitle("LAVASMART")
                .setContentText("YOUR MACHINE IS OFF")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(CHANNEL_ID)
                .build();

        if(notify_request && value.equals("OFF")) {
            notificationManager.notify(notifyID, notification);
            notify_request = false;
        }


    }

    private void createNotificationChannel()

    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "my_channel_01";
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(id,name,importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(channel);
    }





    public void writetoDataBase()
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("LED_STATUS");
    }
    public void readFromDataBase()
    {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                status = findViewById(R.id.led_status);
                value = dataSnapshot.getValue(String.class);
                Log.v("lorenzo","Value is "+value);
                status.setText(value);
                addNotification();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.v("lorenzo","Failed to read value", databaseError.toException());
            }
        });
    }
}
