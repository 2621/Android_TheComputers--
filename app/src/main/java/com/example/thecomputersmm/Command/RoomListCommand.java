package com.example.thecomputersmm.Command;

public class RoomListCommand {
    private Integer id;
    private String name;
    private String lastMessage;


    public RoomListCommand(Integer id, String name, String lastMessage)
    {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {return name;}
    public String getLastMessage() {return lastMessage;}
}
