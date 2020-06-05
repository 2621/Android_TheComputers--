package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thecomputersmm.Adapter.UserListAdapter;
import com.example.thecomputersmm.Command.ChatListCommand;
import com.example.thecomputersmm.Adapter.ChatListAdapter;
import com.example.thecomputersmm.Command.MessageCommand;
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
    ChatListAdapter adapter;

    StringRequest stringRequest;
    JsonArrayRequest jsonArrayRequest;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        requestQueue = Volley.newRequestQueue(this);

        listViewChat = (ListView) findViewById(R.id.listViewChat);

        chats = new ArrayList<ChatListCommand>();

        try {
            loadRooms();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Já está sendo pego tanto as rooms quanto as mensagens
        //Você pode testar isso colocando as duas linhas abaixo por exemplo no método openSearch e testar clicando no botão de +, estamos conseguindo carregar os dados
        //mas acho que tem que ser feito em um thread a parte, não sei...

//        adapter = new ChatListAdapter(this, R.layout.item_chat_list,chats);
//        listViewChat.setAdapter(adapter);


        listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InitialPageActivity.this, ChatActivity.class);
                startActivity(intent);

            }

        });

    }

    public void openSearch (View view){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);

//        adapter = new ChatListAdapter(this, R.layout.item_chat_list,chats);
//        listViewChat.setAdapter(adapter);
    }

    public void loadRooms() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        String requestBody = jsonObject.toString();
        String url = "http://192.168.1.6:8080/getUserRooms";

        loadRoomsConnection(url, requestBody);
    }

    public void loadLastMessage(RoomCommand room) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", room.getId());
        jsonObject.put("name", room.getName());

        String requestBody = jsonObject.toString();
        String url = "http://192.168.1.6:8080/getLastMessage";

        loadLastMessageConnection(url, requestBody, room);
    }

    public void loadRoomsConnection(String url, final String requestBody) throws JSONException{

        //o retorno de getUsers é um JSONArray, e o body é um JsonObject
        jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta onResponse", response.toString());
                        try {
                            parseJSON(response.toString());
                        } catch (JSONException | InterruptedException e) {
                            e.printStackTrace();
                        }
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

    public void loadLastMessageConnection(String url, final String requestBody, final RoomCommand room) {

        //o retorno de getUsers é um JSONObject, e o body é um JsonObject
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resposta onResponse", response.toString());
                        parseJSONRoom(response.toString(), room);
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
        requestQueue.add(jsonObjectRequest);
    }

    private void parseJSON(String jsonString) throws JSONException, InterruptedException {
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomCommand>>(){}.getType();
        List<RoomCommand> roomList = gson.fromJson(jsonString, type);

        Integer i=0;
        for (RoomCommand room : roomList){
            loadLastMessage(room);
            Log.e("for do parsoJson", Integer.toString(i));
            i++;

        }

        Thread.sleep(1000);

        Log.i("passei", "passsei na thread");
        adapter = new ChatListAdapter(this, R.layout.item_chat_list,chats);
        listViewChat.setAdapter(adapter);

    }

    private void parseJSONRoom(String jsonString, RoomCommand room) {

        Gson gson = new Gson();
        Type type = new TypeToken<MessageCommand>(){}.getType();
        MessageCommand lastMessage = gson.fromJson(jsonString, type);

//        chats.add(new ChatListCommand(room.getName(), lastMessage.getContent()));
        Log.i("RoomMessage", room.getName());
        Log.i("LastMessage", lastMessage.getContent());



        Log.d("Estou colocando na view", "passou aqui");

    }

//    public View getView(View view) {
//
//        LayoutInflater inflater=this.getLayoutInflater();
//
//        View rowView=inflater.inflate(R.layout.item_chat_list, null,true);
//
//        TextView nameText = (TextView) rowView.findViewById(R.id.chatName);
//        TextView textText = (TextView) rowView.findViewById(R.id.chatText);
//
////        nameText.setText(room.getName());
////        textText.setText(lastMessage.getContent());
//
//
//        return rowView;
//
//    };


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
