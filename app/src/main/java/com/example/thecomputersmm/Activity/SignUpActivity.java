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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignUpActivity extends AppCompatActivity {

    EditText password;
    EditText confPassword;
    EditText username;

    StringRequest stringRequest;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);

        username = (EditText) findViewById(R.id.editTextUsernameSU);
        password = (EditText) findViewById(R.id.editTextPasswordSU);
        confPassword = (EditText) findViewById(R.id.editTextConfPassword);

        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    private boolean validacaoSenha(String password, String confPassword){
        if(password.equals(confPassword)){
            return true;
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Passwords doesn't match", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
    }

    public void signUp(View view) throws JSONException {

        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String confPasswordString = confPassword.getText().toString();

        if(validacaoSenha(passwordString, confPasswordString)){
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", usernameString);
            jsonBody.put("password", passwordString);
            jsonBody.put("retype", confPasswordString);
            String requestBody = jsonBody.toString();

            String url = Url.newUser;

            signUpConnection(url, requestBody);
        }
    }

    private void signUpConnection(String url, final String requestBody) {

        final Intent intent = new Intent(this, InitialPageActivity.class);
        intent.putExtra("username", username.getText().toString());

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de cadastro", response);
                if (response.equals("true")) {
                    Log.i("cadaster response", response);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), "Registration completed successfully", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "This user is already registered", Toast.LENGTH_LONG);
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

