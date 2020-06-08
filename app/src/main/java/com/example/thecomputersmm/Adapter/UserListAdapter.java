package com.example.thecomputersmm.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Command.UserCommand;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<UserCommand> {
    private final Activity context;

    private final ArrayList<UserCommand> users;

    public ArrayList<String> selectedUsers = new ArrayList<String>();

    public UserListAdapter(Activity context, int layoutItem, ArrayList<UserCommand> users) {
        super(context, layoutItem, users);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.users = users;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.item_user_list, null,true);

        final CheckBox usernameText = (CheckBox) rowView.findViewById(R.id.nameUser);

        usernameText.setText(users.get(position).getUsername());

        //ver se é CompoundButton mesmo ou RadioGrouṕ
        usernameText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked) {
                    selectedUsers.add(usernameText.getText().toString());
                } else{
                    selectedUsers.remove(usernameText.getText().toString());
                }
            }
        });


        return rowView;

    }

    public ArrayList<String> getSelectedUsers(){
        return selectedUsers;
    }
    ;
}
