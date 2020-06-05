package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thecomputersmm.Command.MessageCommand;
import com.example.thecomputersmm.Adapter.MessageListAdapter;
import com.example.thecomputersmm.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private EditText editMessage;

    private ListView listViewMessage;

    private ArrayList<MessageCommand> messages;
    private MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = findViewById(R.id.editMessage);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setDivider(null);
        listViewMessage.setDividerHeight(0);

        messages = new ArrayList<MessageCommand>();
        messages.add(new MessageCommand("me", "Olha essa nova mensagem"));
        messages.add(new MessageCommand("Amigo", "Responded"));

        adapter = new MessageListAdapter(this, messages);


        listViewMessage.setAdapter(adapter);
    }

    public void send(View view){
        Log.d("TAG", "SendMessage");

        String message = editMessage.getText().toString();

        messages.add(new MessageCommand("me", message));
        adapter.notifyDataSetChanged();

        editMessage.setText("");
    }
}
