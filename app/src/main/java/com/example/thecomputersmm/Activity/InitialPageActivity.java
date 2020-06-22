package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thecomputersmm.Adapter.ChatListAdapter;
import com.example.thecomputersmm.Command.MessageCommand;
import com.example.thecomputersmm.Command.RoomCommand;
import com.example.thecomputersmm.Command.RoomListCommand;
import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class InitialPageActivity extends AppCompatActivity {

    private StompClient mStompClient;

    ListView listViewChat;
    String username;

    List<RoomListCommand> roomList;
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
        roomList = new ArrayList<>();

//        chats = new ArrayList<ChatListCommand>();

        connectSocket();

        try {
            loadRooms();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(InitialPageActivity.this, ChatActivity.class);
                intent.putExtra("roomId", roomList.get(position).getId());
                intent.putExtra("roomname", roomList.get(position).getName());
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mStompClient.disconnect();
    }

    @SuppressLint("CheckResult")
    private void connectSocket() {
        String url = Url.webSocket;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        mStompClient.connect();
        mStompClient.topic("/queue/"+ username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(queueMessage -> {
                    JSONObject newJSONRoom = new JSONObject(queueMessage.getPayload());
                        String newJsonRoom = newJSONRoom.toString();
                        Gson gson = new Gson();
                        Type type = new TypeToken<RoomCommand>() {
                        }.getType();
                        RoomCommand newRoom = gson.fromJson(newJsonRoom, type);
                        updateRoomList(newRoom);
                        subscribeOnRoom(newRoom);
                },throwable -> Log.d("fail","Error on subscribe topic",throwable));

    }

    @SuppressLint("CheckResult")
    private void subscribeOnRoom(RoomCommand room){
        mStompClient.topic("/topic/"+ room.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    JSONObject newJSONMessage = new JSONObject(topicMessage.getPayload());
                    String newMessageReceived = newJSONMessage.getString("content");
                    RoomListCommand newRoomListElement = new RoomListCommand(room.getId(), room.getName(), newMessageReceived);
                    updateRoomList(newRoomListElement);
                },throwable -> Log.d("fail","Error on subscribe topic",throwable));

    }

    public void openSearch (View view){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void loadRooms() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        String requestBody = jsonObject.toString();

        String url = Url.getUserRooms;

        loadRoomsConnection(url, requestBody);
    }

    public void loadRoomsConnection(String url, final String requestBody) throws JSONException{

        //o retorno de getUsers é um JSONArray, e o body é um JsonObject
        jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta onResponse", response.toString());
                        try {
                            parseJSONRoom(response.toString());
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

    private void parseJSONRoom(String jsonString) throws JSONException, InterruptedException {
        Gson gson = new Gson();
        Type type = new TypeToken<List<RoomCommand>>(){}.getType();
        List<RoomCommand> roomList = gson.fromJson(jsonString, type);

        Integer roomListSize = roomList.size();
        Log.i("roomListSize", Integer.toString(roomListSize));
        Integer contador = 0;
        for (RoomCommand room : roomList){
            subscribeOnRoom(room);
            loadLastMessage(room, roomListSize, ++contador);
        }
    }

    public void loadLastMessage(RoomCommand room, Integer roomListSize, Integer contador) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", room.getId());
        jsonObject.put("name", room.getName());

        String requestBody = jsonObject.toString();
        String url = Url.getLastMessage;

        loadLastMessageConnection(url, requestBody, room, roomListSize, contador);
    }

    public void loadLastMessageConnection(String url, final String requestBody, final RoomCommand room, Integer roomListSize, Integer contador) {

        //o retorno de getUsers é um JSONObject, e o body é um JsonObject
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resposta onResponse", response.toString());
                        parseJSONLastMessage(response.toString(), room, roomListSize, contador);
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

    private void parseJSONLastMessage(String jsonString, RoomCommand room, Integer roomListSize, Integer contador) {

        Gson gson = new Gson();
        Type type = new TypeToken<MessageCommand>(){}.getType();
        MessageCommand lastMessage = gson.fromJson(jsonString, type);
        this.roomList.add(new RoomListCommand(room.getId(), room.getName(), lastMessage.getContent()));
        if(contador == roomListSize){
            adapter = new ChatListAdapter(this, R.layout.item_chat_list, roomList);
            listViewChat.setAdapter(adapter);
        }
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
    }

    public void openChangePassword (MenuItem item){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void logout (MenuItem item){
        String url = Url.logout;

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

    public void deleteAccount (MenuItem item) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        String requestBody = jsonBody.toString();

        String url = Url.deleteUser;
        deleteAccountConnection(url, requestBody);
    }

    public void deleteAccountConnection(String url, final String requestBody){
        final Intent intent = new Intent(this, LoginActivity.class);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de deletar", response);
                if (response.equals("true")) {
                    Log.i("delete response", response);
                    Toast toast = Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG);
                    toast.show();
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Couldn't delete account", Toast.LENGTH_LONG);
                    Log.i("JSON", requestBody);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        requestQueue.add(stringRequest);
    }

    // A ser chamado no retorno do subscribe
    private void updateRoomList(RoomCommand room) {
        Integer roomListId = findRoomListIdByRoom(room);
        if (roomListId != -1){
            roomList.set(roomListId, createRoomListCommand(room));
        }
        else{
            roomList.add(new RoomListCommand(room.getId(),room.getName(), ""));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateRoomList(RoomListCommand room) {
        Integer roomListId = findRoomListIdByRoom(getRoom(room));
        if (roomListId != -1){
            roomList.set(roomListId, room);
        }
        else{
            roomList.add(new RoomListCommand(room.getId(),room.getName(), room.getLastMessage()));
        }
        adapter.notifyDataSetChanged();
    }

    private Integer findRoomListIdByRoom(RoomCommand room) {
        for (RoomListCommand currentRoom: roomList) {
            if (currentRoom.getId() == room.getId()){
                return roomList.indexOf(currentRoom);
            }
        }
        return -1;
    }

    private RoomListCommand createRoomListCommand(RoomCommand room) {
        return new RoomListCommand(room.getId(),room.getName(), "");
    }

    private RoomListCommand createRoomListCommand(RoomListCommand room, String lastMessage) {
        return new RoomListCommand(room.getId(),room.getName(), lastMessage);
    }

    private RoomCommand getRoom(RoomListCommand room){
        return new RoomCommand(room.getId(), room.getName());
    }
}
