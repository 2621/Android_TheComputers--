package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    ListView listViewUser;

    ArrayList<User_list> users;
    User_listAdapter adapter;

    StringRequest stringRequest;
    RequestQueue requestQueue;


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
    public void nameGroup(View view){
        LayoutInflater inflater = (LayoutInflater) Search.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View pview;
        pview = inflater.inflate(R.layout.name_group_popup, null);

        PopupWindow cp = new PopupWindow(pview, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setFocusable(true);
        cp.showAtLocation(view, Gravity.CENTER, 0, 0);
        cp.update(0, 0, 900, 520);

    }

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
        String url = "http://192.168.1.6:8080/login";

        final Intent intent = new Intent(this, MainActivity.class);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de logout", response);
                if (response.equals("NOT_LOGGED")){
                    Log.i("Logout response", response);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Couldn't logout", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        });

        requestQueue.add(stringRequest);

    }

    public void deleteAccount (MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //COLOCAR AQUI A LÓGICA DO BANCO DE DADOS PARA DELETAR A CONTA (JÁ EFETUA O LOGOUT TAMBÉM
    }
}
