package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.example.thecomputersmm.Activity.InitialPageActivity;
import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.AccessController;

//pegar o usuário que esta cadastrado

public class ChangePasswordActivity<Authentication> extends AppCompatActivity {

    EditText password;
    EditText confPassword;
    String username;

    StringRequest stringRequest;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        password = (EditText) findViewById(R.id.textpass2);
        confPassword = (EditText) findViewById(R.id.textpass);

        requestQueue = Volley.newRequestQueue(this);

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

    public void confirmNewPassword (View view) throws JSONException {
        String passwordString = password.getText().toString();
        String confPasswordString = confPassword.getText().toString();

        if(validacaoSenha(passwordString, confPasswordString)){

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("password", passwordString);
            jsonBody.put("retype", confPasswordString);
            String requestBody = jsonBody.toString();

            String url = Url.updateUserPassword;

            newPassword(url, requestBody);
        }

    }

    private void newPassword(String url, final String requestBody){
        final Intent intent = new Intent(this, InitialPageActivity.class);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta troca de senha", response);
                if(response.equals("true")){
                    Log.i("changePassword response", response);
                    Toast toast = Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT);
                    toast.show();

                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try{
                                Thread.sleep(2500);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Couldn't change password", Toast.LENGTH_LONG);
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
