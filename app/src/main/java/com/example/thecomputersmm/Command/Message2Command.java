package com.example.thecomputersmm.Command;

//public class Message_list {
//    String message = "";
//
//    public Message_list(String message) {
//        this.message = message;
//    }
//}


    public class Message2Command {
        private String idUser;
        private String message;

        public Message2Command(String idUser, String message) {
            this.idUser = idUser;
            this.message = message;
        }

        public String getIdUser() {return  idUser;};

        public void setIdUser(String idUser) {this.idUser = idUser;}

        public String getMessage() {return message;}

        public void setMessage(String message) {this.message = message;}
    }

