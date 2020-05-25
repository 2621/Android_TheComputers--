package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignUp_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);
    }

    public void openInitialPage (View view){
        Intent intent = new Intent(this, InitialPage.class);
        startActivity(intent);
    }
}
