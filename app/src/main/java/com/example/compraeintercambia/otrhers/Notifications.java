package com.example.compraeintercambia.otrhers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.compraeintercambia.ComprarActivity;
import com.example.compraeintercambia.R;
import com.example.compraeintercambia.model.Products;

public class Notifications {
    //constants
    private final String CHANNEL_ID="Notificacion";
    private final int NOTIFICATION_ID=1;

    Products products = new Products();
    private Context context;

    public Notifications(Context context){
        this.context=context;
    }

    public void showNotification(){

        createNontificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_24)
                .setContentTitle("Nuevo artículo.")
                .setContentText("Pulsa para ver los detalles del articulo, podría interesarte")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //start activity comprar
        Intent intentComprar = new Intent(context.getApplicationContext(), ComprarActivity.class);
        //creating the StackBuilder and add the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intentComprar);
        //get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        //notificationId is a unique in for each notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    public void createNontificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            String description = "Start activity from a notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
