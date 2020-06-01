package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class InitialPage extends AppCompatActivity {

    ListView listViewChat;

    ArrayList<Chat_list> chats;
    Chat_listAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        listViewChat = (ListView) findViewById(R.id.listViewChat);
        chats = new ArrayList<Chat_list>();
        chats.add(new Chat_list("Sarah", "Oi"));
        chats.add(new Chat_list("Ale", "Ate mais"));

        adapter = new Chat_listAdapter(this, R.layout.item_chat_list,chats);


        listViewChat.setAdapter(adapter);

        listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InitialPage.this, Chat.class);
                startActivity(intent);

            }

        });
    }

    public void openSearch (View view){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

//    public void openChat (View view){
//        Intent intent = new Intent(this, Chat.class);
//        startActivity(intent);
//    }
}
