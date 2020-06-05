package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thecomputersmm.Command.Message2Command;
import com.example.thecomputersmm.Adapter.MessageListAdapter;
import com.example.thecomputersmm.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private EditText editMessage;
    private TextView editRoomname;

    private ListView listViewMessage;

    private ArrayList<Message2Command> messages;
    private MessageListAdapter adapter;

    private String username;
    private String roomname;
    private Integer roomId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        roomname = extras.getString("roomname");
        roomId = extras.getInt("roomId");

        editRoomname =  (TextView) findViewById(R.id.textViewRoomName);
        editRoomname.setText(roomname);

        editMessage = findViewById(R.id.editMessage);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setDivider(null);
        listViewMessage.setDividerHeight(0);

        messages = new ArrayList<Message2Command>();
        messages.add(new Message2Command("me", "Olha essa nova mensagem"));
        messages.add(new Message2Command("Amigo", "Responded"));

        adapter = new MessageListAdapter(this, messages);


        listViewMessage.setAdapter(adapter);
    }

    public void send(View view){
        Log.d("TAG", "SendMessage");

        String message = editMessage.getText().toString();

        messages.add(new Message2Command("me", message));
        adapter.notifyDataSetChanged();

        editMessage.setText("");
    }
}
