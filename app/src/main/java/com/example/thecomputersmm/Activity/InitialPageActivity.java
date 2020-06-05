package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thecomputersmm.Adapter.UserListAdapter;
import com.example.thecomputersmm.Command.ChatListCommand;
import com.example.thecomputersmm.Adapter.ChatListAdapter;
import com.example.thecomputersmm.Command.RoomCommand;
import com.example.thecomputersmm.Command.UserCommand;
import com.example.thecomputersmm.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InitialPageActivity extends AppCompatActivity {

    ListView listViewChat;
    String username;

    ArrayList<ChatListCommand> chats;
    ArrayList<RoomCommand> rooms;
    ChatListAdapter adapter;

    StringRequest stringRequest;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        requestQueue = Volley.newRequestQueue(this);

        listViewChat = (ListView) findViewById(R.id.listViewChat);

        try {
            loadRooms();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chats = new ArrayList<ChatListCommand>();
        chats.add(new ChatListCommand("Sarah", "Oi"));
        chats.add(new ChatListCommand("Ale", "Ate mais"));

        adapter = new ChatListAdapter(this, R.layout.item_chat_list,chats);


        listViewChat.setAdapter(adapter);

        listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InitialPageActivity.this, ChatActivity.class);
                startActivity(intent);

            }

        });

    }

    public void loadRooms() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        String requestBody = jsonObject.toString();
        String url = "http://192.168.1.6:8080/getUserRooms";

        loadRoomsConnection(url, requestBody);
    }

    public void loadRoomsConnection(String url, final String requestBody) {

        //o retorno de getUsers é um JSONArray, e o body é um JsonObject
        jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta onResponse", response.toString());
                        parseJSON(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volleyError",error.toString());
                    }
                }) { //adicionar um body jsonObject
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void parseJSON(String jsonString) {
        rooms = new ArrayList<RoomCommand>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomCommand>>(){}.getType();
        List<RoomCommand> roomList = gson.fromJson(jsonString, type);
        for (RoomCommand room : roomList){
            rooms.add(new RoomCommand(room.getId(), room.getName()));
            Log.i("rooms", room.getName());
        }
    }

    public void openSearch (View view){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
    }


    public void openChangePassword (MenuItem item){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    public void logout (MenuItem item){
        String url = "http://192.168.1.6:8080/logout";

        final Intent intent = new Intent(this, LoginActivity.class);

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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        //COLOCAR AQUI A LÓGICA DO BANCO DE DADOS PARA DELETAR A CONTA (JÁ EFETUA O LOGOUT TAMBÉM
    }




}
