package com.example.thecomputersmm.Command;

public class ChatListCommand {
    private String roomName;
    private String lastMessage;

    public ChatListCommand(String roomName, String lastMessage) {
        this.roomName = roomName;
        this.lastMessage = lastMessage;
    }

    public String getRoomName(){ return roomName; }

    public String getLastMessage() { return lastMessage; }
}
