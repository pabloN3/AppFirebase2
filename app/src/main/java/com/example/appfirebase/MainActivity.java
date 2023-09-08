package com.example.appfirebase;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    public EditText ettoken,mensaje_recibido,numero_telefono, sms_telefono,sms_mensaje,telefono_llamada_saliente;
    public Switch activador;
    private static final String PHP_URL = "https://www.invirtiendo.com.mx/sia/operaciones/directivas/atencion_clientes_v2_test/mensajes_sms_firebase/registrar_token_sms_firebase.php";
    String token_envio,deviceId,variable_opcion,marca_celular,modelo_celular;
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSIONS_REQUEST_SMS = 123;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceId = DeviceUtils.getDeviceId(this);

        instance = this;
        variable_opcion = "r";//registrar
        //ettoken = findViewById(R.id.editTextTextMultiLine);
        mensaje_recibido = findViewById(R.id.editTextTextMultiLine2);
        numero_telefono = findViewById(R.id.editTextTextMultiLine3);
        sms_telefono    = findViewById(R.id.editTextText);
        sms_mensaje = findViewById(R.id.editTextTextMultiLine);
        telefono_llamada_saliente = findViewById(R.id.editTextPhone);
        modelo_celular = Build.MODEL;
        marca_celular = Build.MANUFACTURER;
        AlertDialog.Builder builder_2 = new AlertDialog.Builder(MainActivity.this);

        activador = (Switch) findViewById(R.id.switch1);
        activador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) //Line A
            {
                if (isChecked) {
                    // Acciones al estar activo
                    System.out.println("Activo");
                    activador.setText("Activo");
                    activador.setTextColor(Color.parseColor("#21732d"));
                    activador.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#21732d")));
                    activador.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#21732d")));
                    variable_opcion = "a";//inactivo
                    System.out.println("Celular activo");
                    sendPostData();
                } else {
                    // Acciones al estar inactivo
                    System.out.println("Inactivo");
                    activador.setText("Inactivo");
                    variable_opcion = "i";//inactivo
                    activador.setTextColor(Color.parseColor("#b2b8b3"));
                    activador.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#b2b8b3")));
                    activador.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#b2b8b3")));
                    System.out.println("Celular inactivo");
                    sendPostData();
                }
            }
        });






        //TextHolder notification_text = new TextHolder(mensaje_recibido);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                System.out.println("Registro FCM fallido");

                                builder_2.setTitle("Error de conexión")
                                        .setMessage("El token de registro no se ha podido generar, debido a la conexión de internet. FAVOR DE REINICIAR LA APLICACION")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Maneja la acción cuando se hace clic en Aceptar
                                                //reiniciarApp();
                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Maneja la acción cuando se hace clic en Cancelar

                                                dialog.dismiss(); // Cierra el cuadro de diálogo
                                            }
                                        })
                                        .show();
                                //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            if (token == ""){
                                builder_2.setTitle("Error de conexión")
                                        .setMessage("El token de registro no se ha podido generar, debido a la conexión de internet.FAVOR DE REINICIAR LA APLICACION")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Maneja la acción cuando se hace clic en Aceptar
                                                //reiniciarApp();
                                            }
                                        })

                                        .show();
                            }
                            else{
                                System.out.println(token);
                                Toast.makeText(MainActivity.this, "El token de registro del dispositivo es: "+token, Toast.LENGTH_SHORT).show();
                                //ettoken.setText(token);
                                //registrarToken(token);
                                //System.out.println("La marca es:"+marca_celular+" y el modelo es:"+modelo_celular);
                                activador.setChecked(true);
                                token_envio = token;
                                variable_opcion = "r";//inactivo
                                sendPostData();


                            }




                        }
                            // Log and toast
                            //String msg = getString(R.string.msg_token_fmt, token);
                            //Log.d(TAG, msg);
                    });



        if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS,},1000);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_SMS);
        }











    }
    @Override
    protected void onResume(){
        super.onResume();
        //variable_opcion = "";//inactivo
        System.out.println("Se volvio a la aplicación");
        //activador.setChecked(true);
        //sendPostData();

    }

    @Override
    protected void onStop(){
        super.onStop();
        variable_opcion = "i";//inactivo
        System.out.println("Se salio de la aplicación");
        activador.setChecked(false);
        sendPostData();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        variable_opcion = "i";//inactivo
        System.out.println("La aplicacion se cerro completamente");
        activador.setChecked(false);
        sendPostData();
    }

    // Añade el método onRequestPermissionsResult para manejar la respuesta de permisos.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes continuar configurando el receptor SMS.
            } else {
                // Permiso denegado, maneja la situación.
            }
        }
    }




    public static MainActivity getInstance() {
        return instance;
    }

    public void setText(){

    }

    private void sendPostData() {
        new SendPostRequest().execute();
    }




    private class SendPostRequest extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {
            try {
                String variable = variable_opcion;
                String info_cel = marca_celular+" >>> "+modelo_celular;
                URL url = new URL(PHP_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Set your POST parameters here

                String postData = "param1="+token_envio+"&param2="+deviceId+"&param3="+variable+"&param4="+info_cel; // Replace with your params
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
                    }


                    // Handle successful response
                    //Toast.makeText(MainActivity.this,"ENTRO",Toast.LENGTH_LONG).show();

                    //System.out.println("Entroo");
                    //Toast.makeText(MainActivity.this,respuesta.toString(),Toast.LENGTH_LONG).show();
                } else {
                    // Handle error response
                    //Toast.makeText(MainActivity.this,"no entrooo",Toast.LENGTH_LONG).show()
                    System.out.println("Error de conexion");
                    CustomAlertDialog customDialog = new CustomAlertDialog(
                            "Error de conexión",
                            "No se ha podido conectar con el servidor. FAVOR DE REINICIAR LA APLICACION",
                            "OK",
                            "Cancelar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //reiniciarApp();
                                    // Manejar la acción positiva
                                    // Por ejemplo, realizar alguna acción al hacer clic en Aceptar
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Manejar la acción negativa
                                    // Por ejemplo, realizar alguna acción al hacer clic en Cancelar
                                    dialog.dismiss(); // Cierra el cuadro de diálogo
                                }
                            });

                    customDialog.show(getSupportFragmentManager(), "custom_dialog_tag");
                }

            } catch (IOException e) {
                e.printStackTrace();
                CustomAlertDialog customDialog2 = new CustomAlertDialog(
                        "Error de conexión",
                        "No se ha podido conectar con el servidor por problemas de internet. FAVOR DE REINICIAR LA APLICACION",
                        "Ok",
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //reiniciarApp();
                                // Manejar la acción positiva
                                // Por ejemplo, realizar alguna acción al hacer clic en Aceptar

                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Manejar la acción negativa
                                // Por ejemplo, realizar alguna acción al hacer clic en Cancelar
                                dialog.dismiss(); // Cierra el cuadro de diálogo
                            }
                        });

                customDialog2.show(getSupportFragmentManager(), "custom_dialog_tag");

            }
            return null;
        }
    }

    private void reiniciarApp() {
        Intent intent = new Intent(this, MainActivity.class); // Cambia "MainActivity" por la actividad principal de tu aplicación
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }


}