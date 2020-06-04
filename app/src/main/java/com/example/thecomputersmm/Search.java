package com.example.thecomputersmm;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity {

    ListView listViewUser;

    ArrayList<User_list> users;
    User_listAdapter adapter;

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

    public void loadUsers() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        String requestBody = jsonObject.toString();
        String url = "http://192.168.1.6:8080/getUsers";

        loadUsersConnection(url, requestBody);
    }

    public void loadUsersConnection(String url, final String requestBody) {

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
        users = new ArrayList<User_list>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<User_list>>(){}.getType();
        List<User_list> userList = gson.fromJson(jsonString, type);
        for (User_list user : userList){
            users.add(new User_list(user.username));
        }
        //carregar na listView os usuários
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
        String url = "http://192.168.1.6:8080/logout";

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