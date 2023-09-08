package com.example.appfirebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    EditText texnot,numnot,llamadanot;

    private static final int REQUEST_CALL_PERMISSION = 1;
    //MainActivity.getInstance().EditText.;
    //Llamada llamada = new Llamada(this);
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //text_sms.findViewById(R.this.layout.activity_main);
        System.out.println("From:"+remoteMessage);

        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            System.out.println("Message Notification Body"+remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getFrom(),remoteMessage.getNotification().getBody());
    }



    private void sendNotification(String from, String body) {
        List<String> chunks;
        String string = body;
        String[] parts = string.split(";");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];



        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {


                if(part3.equals("1")){

                    //enviar_mensajes(part2,part1);
                    //System.out.println(part3);
                    texnot = MainActivity.getInstance().mensaje_recibido;
                    numnot = MainActivity.getInstance().numero_telefono;
                    texnot.setText(part2);
                    numnot.setText(part1);
                    SmsManager sms = SmsManager.getDefault();
                    //sms.sendTextMessage(part1,null,part2,null,null);
                    sms.sendMultipartTextMessage(part1,null,sms.divideMessage(part2),null,null);
                    Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(), "MENSAJE ENVIADO", Toast.LENGTH_LONG).show();




                } else if (part3.equals("3")) {
                    llamadanot = MainActivity.getInstance().telefono_llamada_saliente;
                    llamadanot.setText(part1);
                    String numerotel = "tel:"+part1;
                    makeCallWithSpeaker(numerotel);
                }


                //Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(), from + " -> " + body,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "My channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)

                        .setContentTitle("My new notification")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            System.out.println("entro aqui");
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        System.out.println("entro aqui 2");
    }



    private void makeCallWithSpeaker(String phoneNumber) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);

            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            long callStartTime = System.currentTimeMillis();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
                //callIntent.getAction().toString();

            }
        }
    }

    private void enviar_mensajes(String texto, String numero){

        String originalString = texto;
        String texto_anidado = "";
        int maxLength = 70;
        texnot = MainActivity.getInstance().mensaje_recibido;
        numnot = MainActivity.getInstance().numero_telefono;

        ArrayList<String> cadena = (ArrayList<String>) SplitString(originalString,maxLength);
        //cadena.toArray();

        if (originalString.length() < maxLength){
            //System.out.println(part3);

            texnot.setText(texto);
            numnot.setText(numero);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,texto,null,null);
            Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(), "MENSAJE ENVIADO", Toast.LENGTH_LONG).show();
        }
        else {

            //for(String cadena_mensaje:cadena){
                SmsManager sms = SmsManager.getDefault();
                sms.sendMultipartTextMessage(numero,null, cadena,null,null);
                //texto_anidado += cadena_mensaje;
            //}
            texnot.setText(texto_anidado);
            numnot.setText(numero);

        }

    }

    private List<String> SplitString(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < text.length(); i += maxLength) {
            int end = Math.min(i + maxLength, text.length());
            chunks.add(text.substring(i, end));
        }

        return chunks;
    }
}
