package com.example.thecomputersmm.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Command.UserCommand;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<UserCommand> {
    private final Activity context;

    private final ArrayList<UserCommand> users;

    public UserListAdapter(Activity context, int layoutItem, ArrayList<UserCommand> users) {
        super(context, layoutItem, users);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.users = users;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.item_user_list, null,true);

        TextView usernameText = (TextView) rowView.findViewById(R.id.nameUser);

        usernameText.setText(users.get(position).username);


        return rowView;

    };
}
