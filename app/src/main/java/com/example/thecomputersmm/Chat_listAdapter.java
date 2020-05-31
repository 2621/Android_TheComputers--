package com.example.thecomputersmm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Chat_listAdapter extends ArrayAdapter<Chat_list> {
    private final Activity context;

    private final ArrayList<Chat_list> chats;

    public Chat_listAdapter(Activity context, int layoutItem, ArrayList<Chat_list> chats) {
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


        nameText.setText(chats.get(position).name);
        textText.setText(chats.get(position).text);


        return rowView;

    };
}
