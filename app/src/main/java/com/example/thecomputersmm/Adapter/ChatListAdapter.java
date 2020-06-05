package com.example.thecomputersmm.Adapter;

import com.example.thecomputersmm.Command.ChatListCommand;
import com.example.thecomputersmm.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListAdapter extends ArrayAdapter<ChatListCommand> {
    private final Activity context;

    private final ArrayList<ChatListCommand> chats;

    public ChatListAdapter(Activity context, int layoutItem, ArrayList<ChatListCommand> chats) {
        super(context, layoutItem, chats);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.chats = chats;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.item_chat_list, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.chatName);
        TextView textText = (TextView) rowView.findViewById(R.id.chatText);


        nameText.setText(chats.get(position).roomName);
        textText.setText(chats.get(position).lastMessage);


        return rowView;

    };
}
