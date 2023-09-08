package com.example.appfirebase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

public class ConexionServidorWeb extends AsyncTask<Void,Void,String > {
    private static final String PHP_SCRIPT_URL = "https://www.invirtiendo.com.mx/sia/operaciones/directivas/atencion_clientes_v2_test/mensajes_sms_firebase/registrar_token_sms_firebase.php";
    private static final String PARAM_1 = "1";
    private static final String PARAM_2 = "2";

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";
        HttpURLConnection connection = null;

        try {
            URL url = new URL(PHP_SCRIPT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            Map<String, String> params = new HashMap<>();
            params.put("param1", PARAM_1);
            params.put("param2", PARAM_2);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(getParamsString(params));
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder responseBuilder = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }

                in.close();
                response = responseBuilder.toString();
            } else {
                response = "Error: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Handle the response from the PHP script
    }

    private String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("&");
        }
        return result.toString();
    }
}
