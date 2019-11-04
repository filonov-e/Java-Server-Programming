package app;

import java.util.Date;

public class Message {
    private String sender;
    private String message;
    private Date date;
    private String[] sports;

    public Message(String sender, String message, Date date, String[] sports) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.sports = sports;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getSports() {
        return sports;
    }

    public void setSports(String[] sports) {
        this.sports = sports;
    }
}