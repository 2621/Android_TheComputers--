package com.example.thecomputersmm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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

import com.example.thecomputersmm.Adapter.UserListAdapter;
import com.example.thecomputersmm.Command.RoomCommand;
import com.example.thecomputersmm.R;
import com.example.thecomputersmm.Command.UserCommand;
import com.example.thecomputersmm.Url;

import com.example.thecomputersmm.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ListView listViewUser;
    EditText roomname;
    Integer roomId;

    ArrayList<UserCommand> users;
    UserListAdapter adapter;
    ArrayList<String> selectedUsers;

    StringRequest stringRequest;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        listViewUser = (ListView) findViewById(R.id.listViewUser);

        requestQueue = Volley.newRequestQueue(this);

        try {
            loadUsers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        finish();
    }

    public void loadUsers() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        String requestBody = jsonObject.toString();
        String url = Url.getUsers;

        loadUsersConnection(url, requestBody);
    }

    public void loadUsersConnection(String url, final String requestBody) {

        //o retorno de getUsers é um JSONArray, e o body é um JsonObject
        jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta onResponse", response.toString());
                        parseJSONUsers(response.toString());
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

    private void parseJSONUsers(String jsonString) {
        users = new ArrayList<UserCommand>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<UserCommand>>(){}.getType();
        List<UserCommand> userList = gson.fromJson(jsonString, type);

        for (UserCommand user : userList){
            users.add(new UserCommand(user.getUsername()));
        }

        adapter = new UserListAdapter(this, R.layout.item_user_list,users);
        listViewUser.setAdapter(adapter);
    }

    public void roomName(View view) throws JSONException {

        selectedUsers = adapter.getSelectedUsers();

        if(selectedUsers.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please select users to your chat", Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            LayoutInflater inflater = (LayoutInflater) SearchActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

            View pview;
            pview = inflater.inflate(R.layout.name_group_popup, null);

            PopupWindow cp = new PopupWindow(pview, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.setFocusable(true);
            cp.showAtLocation(view, Gravity.CENTER, 0, 0);
            cp.update(0, 0, 900, 520);

            roomname = (EditText) pview.findViewById(R.id.roomname);
        }
    }

    public void addUsers() throws JSONException {
        addUserToRoom(username); //adiciona o proprio usuario que criou a sala
        for (String checkedItem: selectedUsers) {
            addUserToRoom(checkedItem);
        }
    }

    public void getRoom() throws JSONException{
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("roomName", roomname.getText().toString());

        String requestBody = jsonBody.toString();
        String url = Url.getRoom;
        Log.d("passou pelo getRoom", "aqui");
        getRoomConnection(url, requestBody);
    }

    public void getRoomConnection(String url, final String requestBody){
        final Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("roomname", roomname.getText().toString());

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de get room", response);
                parseJSONRoom(response);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Não deu certo","aqui");
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

    public void parseJSONRoom(String jsonMessage){
        Gson gson = new Gson();
        Type type = new TypeToken<RoomCommand>() {
        }.getType();
        RoomCommand roomCommand = gson.fromJson(jsonMessage, type);
        roomId = roomCommand.getId();
    }

    public void addUserToRoom(String username) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("roomName", roomname.getText().toString());

        String requestBody = jsonBody.toString();
        String url = Url.addUserToRoom;

        addUserToRoomConnection(url, requestBody, username);
    }

    public void addUserToRoomConnection(String url, final String requestBody, final String username){
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de add user", response);
                if (response.equals("false")){
                    Toast toast = Toast.makeText(getApplicationContext(), username + "cannot added to this chat", Toast.LENGTH_LONG);
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

    public void createRoom(View view) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("roomName", roomname.getText().toString());

        String requestBody = jsonBody.toString();
        String url = Url.createRoom;

        createRoomConnection(url, requestBody);
    }

    public void createRoomConnection(String url, final String requestBody){

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Resposta de create room", response);
                if (response.equals("true")){
                    try{
                        getRoom();
                        addUsers();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "This chat name is already in use. Please, choose another one.", Toast.LENGTH_LONG);
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
                    PreferencesUtils.saveUsername("",SearchActivity.this);
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

        String url = Url.removeUser;
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
                    PreferencesUtils.saveUsername("",SearchActivity.this);
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
}