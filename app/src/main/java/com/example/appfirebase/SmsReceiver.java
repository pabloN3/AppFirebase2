package com.example.appfirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    EditText texnot,numnot;
    String numero_telefonico, contenido_sms, sms_contenido;
    private static final String PHP_URL = "https://www.invirtiendo.com.mx/sia/operaciones/directivas/atencion_clientes_v2_test/mensajes_sms_firebase/peticiones_app/registrar_sms_llamada_entrante.php";
    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        texnot = MainActivity.getInstance().sms_mensaje;
        numnot = MainActivity.getInstance().sms_telefono;
        System.out.println("Se está recibiendo mensaje");
        if (intent.getAction() != null && intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String message = smsMessage.getDisplayMessageBody();
                        texnot.setText(message);
                        numnot.setText(sender);
                        System.out.println("El telefono es: ->"+sender+" y el mensaje es: "+message);
                        Log.d("SmsReceiver", "Sender: " + sender);
                        Log.d("SmsReceiver", "Message: " + message);
                        // Aquí puedes manejar el SMS entrante, como mostrar una notificación, almacenar el mensaje, etc.
                    }
                }
            }
        }
    }*/
    @Override
    public void onReceive(Context context, Intent intent) {

        String validador_numero = "^(?:\\+\\d{1,3}\\s?)?\\d{10}$";
        Pattern pattern = Pattern.compile(validador_numero);

        texnot = MainActivity.getInstance().sms_mensaje;
        numnot = MainActivity.getInstance().sms_telefono;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object[] pdus = (Object[]) extras.get("pdus");
            if (pdus != null) {
                StringBuilder fullMessage = new StringBuilder(); // Para almacenar el mensaje completo
                String number_origin = null; // Variable para alma
                for (Object pdu : pdus) {
                    //SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, extras.getString("format"));
                    //String encryptedText = smsMessage.getMessageBody();
                    //String number_origin = smsMessage.getOriginatingAddress();
                    // Aquí continuará el proceso de descifrado
                    if (number_origin == null) {
                        number_origin = smsMessage.getOriginatingAddress();
                    }

                    // Concatenar el cuerpo de cada parte del mensaje
                    String encryptedText = smsMessage.getMessageBody();
                    fullMessage.append(encryptedText);

                    //if (number_origin.matches(validador_numero)){

                    //}
                }
                assert number_origin != null;
                Matcher matcher = pattern.matcher(number_origin);
                if (matcher.matches()){
                    numero_telefonico = number_origin;
                    contenido_sms = String.valueOf(fullMessage);
                    texnot.setText(fullMessage);
                    numnot.setText(number_origin);

                    Log.d("SmsReceiver", "Sender: " + number_origin);
                    Log.d("SmsReceiver", "Message: " + contenido_sms);
                    sendPostData();//241113
                }
                else{
                    Toast.makeText(context.getApplicationContext(), "MENSAJE ENTRANTE NO VALIDO",Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private void sendPostData() {
        new SmsReceiver.SendPostRequest().execute();
    }




    private class SendPostRequest extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {

                URL url = new URL(PHP_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Set your POST parameters here

                String postData = "param1="+numero_telefonico+"&param2="+contenido_sms+"&param3=2"; // Replace with your params
                System.out.println(postData);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        System.out.println("Respuesta del servidor: " + response.toString());
                        //Toast.makeText(SmsReceiver.this.ge,  response.toString(),Toast.LENGTH_SHORT).show();

                    }


                    // Handle successful response
                    //Toast.makeText(MainActivity.this,"ENTRO",Toast.LENGTH_LONG).show();

                    //System.out.println("Entroo");
                    //Toast.makeText(MainActivity.this,respuesta.toString(),Toast.LENGTH_LONG).show();
                } else {
                    // Handle error response
                    //Toast.makeText(MainActivity.this,"no entrooo",Toast.LENGTH_LONG).show();
                    System.out.println("Error de conexion");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
