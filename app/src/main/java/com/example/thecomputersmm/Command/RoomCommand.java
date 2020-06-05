package com.example.thecomputersmm.Command;

public class RoomCommand {
    private Integer id;
    private String name;

    public RoomCommand(Integer id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {return name;}
}
