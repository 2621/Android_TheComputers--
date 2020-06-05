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

    public MessageCommand () {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
