package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    ListView listViewMessage;

    ArrayList<Message_list> messages;
    Message_listAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        messages = new ArrayList<Message_list>();
        messages.add(new Message_list("Olha essa nova mensagem"));

        adapter = new Message_listAdapter(this, R.layout.item_message_list,messages);


        listViewMessage.setAdapter(adapter);
    }

    public void send(View view){
        messages.add(new Message_list("Nova menssagem enviada"));
        adapter.notifyDataSetChanged();
    }
}
