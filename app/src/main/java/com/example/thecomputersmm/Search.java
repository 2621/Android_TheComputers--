package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    ListView listViewUser;

    ArrayList<User_list> users;
    User_listAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listViewUser = (ListView) findViewById(R.id.listViewUser);
        users = new ArrayList<User_list>();
        users.add(new User_list("Ale"));
        users.add(new User_list("Milena"));
        users.add(new User_list("Sarah"));


        adapter = new User_listAdapter(this, R.layout.item_user_list,users);


        listViewUser.setAdapter(adapter);

    }

    public void openChat (View view){
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
    }
}
