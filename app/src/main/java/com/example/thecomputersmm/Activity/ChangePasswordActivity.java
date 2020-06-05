package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.thecomputersmm.Activity.InitialPageActivity;
import com.example.thecomputersmm.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);
    }

    public void confirmNewPassword (View view){
        Toast toast = Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT);
        toast.show();

        final Intent intent = new Intent(this, InitialPageActivity.class);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2500);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
