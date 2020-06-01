package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private TextView textViewName;
    private EditText editMessage;

    private ListView listViewMessage;

    private ArrayList<Message_list> messages;
    private Message_listAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = findViewById(R.id.editMessage);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setDivider(null);
        listViewMessage.setDividerHeight(0);

        messages = new ArrayList<Message_list>();
        messages.add(new Message_list("me", "Olha essa nova mensagem"));
        messages.add(new Message_list("Amigo", "Responded"));

        adapter = new Message_listAdapter(this, messages);


        listViewMessage.setAdapter(adapter);
    }

    public void send(View view){
        Log.d("TAG", "SendMessage");

        String message = editMessage.getText().toString();

        messages.add(new Message_list("me", message));
        adapter.notifyDataSetChanged();

        editMessage.setText("");
    }
}
