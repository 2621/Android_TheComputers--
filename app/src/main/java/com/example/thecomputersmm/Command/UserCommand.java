package com.example.thecomputersmm.Command;

public class UserCommand {
    public String username;

    public UserCommand(String name) {
        this.username = name;
    }

    public String get(){
        return username;
    }

}
