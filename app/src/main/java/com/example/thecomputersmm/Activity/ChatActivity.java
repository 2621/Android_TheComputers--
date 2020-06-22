package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thecomputersmm.Adapter.MessageListAdapter;
import com.example.thecomputersmm.Command.ChatInfoCommand;
import com.example.thecomputersmm.Command.MessageCommand;
import com.example.thecomputersmm.Command.RoomCommand;
import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonObjectRequest;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private EditText editMessage;
    private TextView editRoomname;
    private ListView listViewMessage;

    private ArrayList<MessageCommand> messages = new ArrayList<>();
    private MessageListAdapter adapter;

    private String username;
    private String roomName;
    private Integer roomId;
    private Integer userId;

    RequestQueue requestQueue;
    private StompClient mStompClient;

    @SuppressLint({"WrongViewCast", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        roomName = extras.getString("roomname");
        roomId = extras.getInt("roomId");

        connectSocket();

        requestQueue = Volley.newRequestQueue(this);
        editRoomname =  (TextView) findViewById(R.id.textViewRoomName);
        editRoomname.setText(roomName);
        editMessage = findViewById(R.id.editMessage);
        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setDivider(null);
        listViewMessage.setDividerHeight(0);
        //inicializa userId e as mensagens que já existem na página
        try{
            getUserId();
            loadMessages();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    private void connectSocket(){
        //está conectando
        String url = Url.webSocket;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        mStompClient.connect();
        mStompClient.topic("/topic/"+roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    JSONObject newJSONMessage = new JSONObject(topicMessage.getPayload());
                    String newMessageReceived = newJSONMessage.toString();
                    Gson gson = new Gson();
                    Type type = new TypeToken<MessageCommand>() {
                    }.getType();
                    MessageCommand newMessage = gson.fromJson(newMessageReceived, type);
                    updateListView(newMessage);
                },throwable -> Log.d("fail","Error on subscribe topic",throwable));
    }

    private void updateListView(MessageCommand newMessageReceived) {
        messages.add(newMessageReceived);
        adapter.notifyDataSetChanged();
    }

    private void loadMessages() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", roomId);
        jsonObject.put("name", roomName);
        String url = Url.getMessages;
        loadMessagesConnection(url, jsonObject);
    }

    private void loadMessagesConnection(String url, final JSONObject requestBody) {
        //o retorno de getMessages é um JSONArray, e o body é um JsonObject
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Resposta loadMessages", response.toString());
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

    public void parseJSONMessages(String jsonMessages) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<MessageCommand>>() {
        }.getType();
        messages = gson.fromJson(jsonMessages, type);
        //testa se há mensagens na sala
        if(messages.size()==1 && isNullMessage()) messages = new ArrayList<MessageCommand>();
        adapter = new MessageListAdapter(this, messages, username);
        listViewMessage.setAdapter(adapter);
    }

    public void getUserId() throws JSONException {
        Log.d("username", username);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        String requestBody = jsonBody.toString();
        String url = Url.getUserId;
        getUserIdConnection(url, requestBody);
    }

    //pra conseguir o id do usuário. Está funcionando
    public void getUserIdConnection(String url, final String requestBody){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("getUserConnection", response);
                userId = Integer.parseInt(response);
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

    private Boolean isNullMessage(){
        return messages.get(0).getContent().isEmpty() &&
                messages.get(0).getRoomId() == 0 &&
                messages.get(0).getUserId() == 0 &&
                messages.get(0).getUsername().isEmpty();
    }

    public void send(View view){
        if (!mStompClient.isConnected()) {
            Log.d("stompCliente", "n conectado");
            return;
        }
        String newMessageSend = editMessage.getText().toString();
        editMessage.setText("");
        JSONObject temp  = new JSONObject();
        try {
            temp.put("userId",userId);
            temp.put("roomId",roomId);
            temp.put("username",username);
            temp.put("content", newMessageSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("massageCommand", String.valueOf(temp));
        mStompClient.send("/app/message", String.valueOf(temp)).subscribe();
    }
}

