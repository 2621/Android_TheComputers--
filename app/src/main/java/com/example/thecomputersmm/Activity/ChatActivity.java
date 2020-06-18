package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
    private ChatInfoCommand chatInfo;
    private MessageListAdapter adapter;

    private String username;
    private String roomName;
    private Integer roomId;
    private Integer userId;

    RequestQueue requestQueue;
    StringRequest stringRequest;

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    JSONObject newJSONMessage;
    String newMessageReceived;
    String newMessageSend;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        roomName = extras.getString("roomname");
        roomId = extras.getInt("roomId");

        //conferir url do websockt, não entendi pq é /mywebsockets/websocket, não está conectando
        String url = Url.webSocket;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        requestQueue = Volley.newRequestQueue(this);
        Log.d("depois do stomp", "aqui");
        try {
            getUserId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("depois do chat info", "aqui");



        editRoomname =  (TextView) findViewById(R.id.textViewRoomName);
        editRoomname.setText(roomName);

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

    void updateListView() {
        messages.add(new MessageCommand(newMessageReceived, username, userId, roomId));
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

    public void getUserId() throws JSONException {

        Log.d("username", username);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);

        String requestBody = jsonBody.toString();

        String url = Url.getUserId;

        getUserIdConnection(url, requestBody);
    }

    //pra conseguir o id do usuário, não foi testado
    public void getUserIdConnection(String url, final String requestBody){

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("getUserConnection", response);
                if (response.equals("LOGGED")){
                    userId = Integer.parseInt(response);
                    subscribe();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "This user is not registered", Toast.LENGTH_LONG);
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


    private void subscribe() {
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions();
        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            break;
                        case ERROR:
                            Log.e("ChatActivity","Stomp connection error",lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e("ChatActivity","Stomp hearthbeat fail",lifecycleEvent.getException());
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        Disposable dispTopic = mStompClient.topic("/topic/"+roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    newJSONMessage = new JSONObject(topicMessage.getPayload());
                    try{
                        newMessageReceived = newJSONMessage.getString("content");
                        // linha onde podemos saber o remetente newJSONMessage.getInt("username")
                        updateListView();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                },throwable -> Log.d("fail","Error on subscribe topic",throwable));

        compositeDisposable.add(dispTopic);

        mStompClient.connect(null);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void send(View view){

        if (!mStompClient.isConnected()) {
            Log.d("stompCliente", "n conectado");
            return;
        }

        newMessageSend = editMessage.getText().toString();
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

        //Conferir esse destino, só peguei do projeto do gv, acho que pra gente é /messages
        compositeDisposable.add(mStompClient.send("/app/message",String.valueOf(temp))
                .compose(applySchedulers())
                .subscribe(() -> Log.d("Chat Activity","STOMP echo send successfully"),
                        throwable -> Log.e("Chat Activity","Error send STOMP ",throwable)));

    }
}

