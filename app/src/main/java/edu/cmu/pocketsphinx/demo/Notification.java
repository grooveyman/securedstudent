package edu.cmu.pocketsphinx.demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;

public class Notification {
    Context ctx;

    public Notification(Context ctx) {
        this.ctx = ctx;
    }

    public Notification(){

    }


    public void tell_user(String Tittle, String Message, ChildEventListener ctc, Class nextCtx){
        final String CHANNEL_ID ="Notificatioon" ;

        //check os version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Broadcast", importance);
            channel.setDescription("All is well");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(ctx, nextCtx);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(Tittle)
                .setContentText(Message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);



        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ctx);
        managerCompat.notify(999,builder.build());



    }
}
