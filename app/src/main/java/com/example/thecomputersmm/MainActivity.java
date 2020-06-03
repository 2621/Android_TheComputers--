package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    EditText password;
    EditText username;

    StringRequest stringRequest;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);

        requestQueue = Volley.newRequestQueue(this);
    }

    public void openSignUpScreen (View view){

        //para chamar a tela de cadastro e passar os parâmetros de username e password
        Intent intent = new Intent(this, SignUp_Screen.class);

        startActivity(intent);
    }

    public void openInitialPage (View view) throws JSONException {
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        //pra testar sem acesso ao bd
//        Intent intent = new Intent(this, InitialPage.class);
//        startActivity(intent);

        //comentar daqui pra baixo pra acessar sem bd
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", usernameString);
        jsonBody.put("password", passwordString);
        String requestBody = jsonBody.toString();

        String url = "http://192.168.1.6:8080/login";

        login(url, requestBody);

    }

    private void login(String url, final String requestBody){

        final Intent intent = new Intent(this, InitialPage.class);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de login", response);
                if (response.equals("LOGGED")){
                  Log.i("Login response", response);
                  startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "This user is not registered", Toast.LENGTH_LONG);
                    Log.i("JSON", requestBody);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        requestQueue.add(stringRequest);

    }



}
