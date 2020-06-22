package com.example.thecomputersmm;

public class Url {
    static public String ip = "34.95.139.21:8080/";
    //Sarah:"192.168.1.6:8080/"
    //Milena: "192.168.15.10:8080/"

    static public String login = "http://" + ip + "login";
    static public String newUser = "http://" + ip + "newUser";
    static public String getUsers = "http://" + ip + "getUsers";
    static public String createRoom = "http://" + ip + "createRoom";
    static public String logout = "http://" + ip + "logout";
    static public String getUserRooms = "http://" + ip + "getUserRooms";
    static public String getLastMessage = "http://" + ip + "getLastMessage";
    static public String addUserToRoom = "http://" + ip + "addUserToRoom";
    static public String updateUserPassword = "http://" + ip + "updateUserPassword";
    static public String removeUser = "http://" + ip + "removeUser";
    static public String getMessages = "http://" + ip + "getMessages";
    static public String getUserId = "http://" + ip + "getUserId";
    static public String webSocket = "ws://" + ip + "mywebsockets/websocket";
    static public String getRoom = "http://" + ip + "getRoom";
}

