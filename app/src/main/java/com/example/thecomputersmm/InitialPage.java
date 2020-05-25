package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InitialPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);
    }

    public void openSearch (View view){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    public void openChat (View view){
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
    }
}
