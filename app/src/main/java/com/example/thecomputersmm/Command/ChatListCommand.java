package com.example.thecomputersmm.Command;

public class ChatListCommand {
    public String roomName;
    public String lastMessage;

    public ChatListCommand(String roomName, String lastMessage) {
        this.roomName = roomName;
        this.lastMessage = lastMessage;
    }
}
