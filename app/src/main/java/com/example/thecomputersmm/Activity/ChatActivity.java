package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thecomputersmm.Adapter.MessageListAdapter;
import com.example.thecomputersmm.Command.MessageCommand;
import com.example.thecomputersmm.Command.RoomCommand;
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

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private EditText editMessage;
    private TextView editRoomname;
    private ListView listViewMessage;

    private ArrayList<MessageCommand> messages = new ArrayList<>();
    private MessageListAdapter adapter;

    private String username;
    private String roomname;
    private Integer roomId;

    RequestQueue requestQueue;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        requestQueue = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        roomname = extras.getString("roomname");
        roomId = extras.getInt("roomId");

        editRoomname =  (TextView) findViewById(R.id.textViewRoomName);
        editRoomname.setText(roomname);

        editMessage = findViewById(R.id.editMessage);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setDivider(null);
        listViewMessage.setDividerHeight(0);

        try{
            loadMessages();
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }

    private void loadMessages() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", roomId);
        jsonObject.put("name", roomname);
        String url = Url.getMessages;
        loadMessagesConnection(url, jsonObject);
    }

    private void loadMessagesConnection(String url, final JSONObject requestBody) {
        //o retorno de getUsers é um JSONArray, e o body é um JsonObject
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta onResponse", response.toString());
                        parseJSONMessages(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("requestBody", requestBody.toString());
                        Log.e("volleyError",error.toString());
                    }
                }){
            @Override
            public byte[] getBody(){
                try {
                    return requestBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public void parseJSONMessages(String jsonMessages){
        Gson gson = new Gson();
        Type type = new TypeToken<List<MessageCommand>>(){}.getType();
        messages = gson.fromJson(jsonMessages, type);

        adapter = new MessageListAdapter(this, messages, username);
        listViewMessage.setAdapter(adapter);
    }

    public void send(View view){
        Log.d("TAG", "SendMessage");

        String message = editMessage.getText().toString();

//        messages.add(new MessageCommand("me", message));
        adapter.notifyDataSetChanged();

        editMessage.setText("");
    }
}
