package com.example.thecomputersmm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class User_listAdapter extends ArrayAdapter<User_list> {
    private final Activity context;

    private final ArrayList<User_list> users;

    public User_listAdapter(Activity context, int layoutItem, ArrayList<User_list> users) {
        super(context, layoutItem, users);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.users = users;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.item_user_list, null,true);

        TextView usernameText = (TextView) rowView.findViewById(R.id.nameUser);

        usernameText.setText(users.get(position).name);


        return rowView;

    };
}
