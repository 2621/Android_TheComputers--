package com.example.thecomputersmm.Adapter;

import com.example.thecomputersmm.Command.MessageCommand;
import com.example.thecomputersmm.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageListAdapter extends ArrayAdapter<MessageCommand> {
    private final Activity context;
    private String username;

    private final ArrayList<MessageCommand> messages;

//    public Message_listAdapter(Activity context, int layoutItem, ArrayList<Message_list> messages) {
//        super(context, layoutItem, messages);
//        // TODO Auto-generated constructor stub
//
//        this.context=context;
//        this.messages = messages;
//
//    }
    public MessageListAdapter(Activity context, ArrayList<MessageCommand> messages, String username) {
        super(context, 0, messages);
        // TODO Auto-generated constructor stub
        this.username = username;
        this.context = context;
        this.messages = messages;

    }

//    public View getView(int position, View view, ViewGroup parent) {
//
//        LayoutInflater inflater=context.getLayoutInflater();
//
//        View rowView=inflater.inflate(R.layout.item_messageUser_list, null,true);
//
//        TextView messageText = (TextView) rowView.findViewById(R.id.message);
//
//        messageText.setText(messages.get(position).message);
//
//
//
//        return rowView;
//
//    };
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= context.getLayoutInflater();

        View rowView;

        if(messages.get(position).getUsername().compareTo(username) == 0){
            //Mensagens do Usuário
            rowView = inflater.inflate(R.layout.item_message_user_list, null,true);
        }

        else{
            //Mensagens dos amigos
            rowView = inflater.inflate(R.layout.item_message_friend_list, null,true);

            TextView messageText = (TextView) rowView.findViewById(R.id.friend);

            messageText.setText(messages.get(position).getUsername());
        }

        TextView messageText = (TextView) rowView.findViewById(R.id.message);

        messageText.setText(messages.get(position).getContent());


        return rowView;

    };
}
