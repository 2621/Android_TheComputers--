package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSignUpScreen (View view){
        //para chamar a tela de cadastro
        Intent intent = new Intent(this, SignUp_Screen.class);
        startActivity(intent);
    }

    public void openInitialPage (View view){
        Intent intent = new Intent(this, InitialPage.class);
        startActivity(intent);
    }

}
