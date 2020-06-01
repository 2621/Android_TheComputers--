package com.example.thecomputersmm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Message_listAdapter extends ArrayAdapter<Message_list> {
    private final Activity context;

    private final ArrayList<Message_list> messages;

//    public Message_listAdapter(Activity context, int layoutItem, ArrayList<Message_list> messages) {
//        super(context, layoutItem, messages);
//        // TODO Auto-generated constructor stub
//
//        this.context=context;
//        this.messages = messages;
//
//    }
    public Message_listAdapter(Activity context, ArrayList<Message_list> messages) {
        super(context, 0, messages);
        // TODO Auto-generated constructor stub

        this.context=context;
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

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView;

        if(messages.get(position).getIdUser().compareTo("me") == 0){
            //Mensagens do Usuário
            rowView = inflater.inflate(R.layout.item_message_user_list, null,true);
        }

        else{
            //Mensagens dos amigos
            rowView = inflater.inflate(R.layout.item_message_friend_list, null,true);

            TextView messageText = (TextView) rowView.findViewById(R.id.friend);

            messageText.setText(messages.get(position).getIdUser());
        }

        TextView messageText = (TextView) rowView.findViewById(R.id.message);

        messageText.setText(messages.get(position).getMessage());


        return rowView;

    };
}
