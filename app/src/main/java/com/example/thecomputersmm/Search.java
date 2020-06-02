package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    ListView listViewUser;

    ArrayList<User_list> users;
    User_listAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listViewUser = (ListView) findViewById(R.id.listViewUser);
        users = new ArrayList<User_list>();
        users.add(new User_list("Ale"));
        users.add(new User_list("Milena"));
        users.add(new User_list("Sarah"));


        adapter = new User_listAdapter(this, R.layout.item_user_list,users);


        listViewUser.setAdapter(adapter);

    }

    //ARRUMAR O POPUO WINDOW
//    public void nameGroup(View view){
//        LayoutInflater inflater = (LayoutInflater) Search.this.getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        View pview;
//        pview = inflater.inflate(R.layout.name_group_popup, null);
//
//        PopupWindow cp = new PopupWindow(pview, ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        cp.showAtLocation(view, Gravity.CENTER, 0, 0);
//      //  cp.update(0, 0, 500, 350);
//
//    }

    public void openChat (View view){
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
    }


    public void openChangePassword (MenuItem item){
        Intent intent = new Intent(this, Change_Password.class);
        startActivity(intent);
    }

    public void logout (MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //COLOCAR AQUI A LÓGICA DO BANCO DE DADOS PARA EFETUAR O LOGOUT
    }

    public void deleteAccount (MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //COLOCAR AQUI A LÓGICA DO BANCO DE DADOS PARA DELETAR A CONTA (JÁ EFETUA O LOGOUT TAMBÉM
    }
}
