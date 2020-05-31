package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends AppCompatActivity {

    EditText password;
    EditText username;

    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void openSignUpScreen (View view){

        //para chamar a tela de cadastro e passar os parâmetros de username e password
        Intent intent = new Intent(this, SignUp_Screen.class);

        startActivity(intent);
    }

    public void openInitialPage (View view){
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        Intent intent = new Intent(this, InitialPage.class);
        intent.putExtra("username", usernameString);
        startActivity(intent);
    }

}
