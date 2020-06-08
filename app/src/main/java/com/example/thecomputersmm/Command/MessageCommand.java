package com.example.thecomputersmm.Command;

public class MessageCommand {
    private String content;
    private String username;
    private Integer userId;
    private Integer roomId;

    public MessageCommand(String content, String username, Integer userId, Integer roomId)
    {
        this.content = content;
        this.username = username;
        this.userId = userId;
        this.roomId = roomId;
    }

    public String getContent() { return content; }

    public String getUsername() { return username; }

    public Integer getUserId() { return userId; }

    public Integer getRoomId() {
        return roomId;
    }
}
