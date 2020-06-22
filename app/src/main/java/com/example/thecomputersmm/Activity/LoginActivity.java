package com.example.thecomputersmm.Activity;

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

import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Url;
import com.example.thecomputersmm.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    EditText password;
    EditText username;

    StringRequest stringRequest;
    RequestQueue requestQueue;

    String logged_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);

        logged_username = PreferencesUtils.getUsername(this);


        if (!logged_username.equals("")){
            Intent intent = new Intent(LoginActivity.this, InitialPageActivity.class);
            intent.putExtra("username", logged_username);
            startActivity(intent);
        } else{
            requestQueue = Volley.newRequestQueue(this);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public void openSignUpScreen (View view){

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void login(View view) throws JSONException {

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username.getText().toString());
        jsonBody.put("password", password.getText().toString());
        String requestBody = jsonBody.toString();

        String url = Url.login;

        loginConnection(url, requestBody);
    }

    public void loginConnection(String url, final String requestBody){

        final Intent intent = new Intent(this, InitialPageActivity.class);
        intent.putExtra("username", username.getText().toString());

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de login", response);
                if (response.equals("LOGGED")){
                  Log.i("Login response", response);
                  PreferencesUtils.saveUsername(username.getText().toString(),LoginActivity.this);
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
