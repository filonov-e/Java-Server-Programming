package app;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String sender;
    private String message;
    private Date date;
    private String[] sports;
    private String[] views;

    public Message(String sender, String message, Date date, String[] sports, String[] views) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.sports = sports;
        this.views = views;
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

    public String[] getViews() {
        return views;
    }

    public void setViews(String[] views) {
        this.views = views;
    }
}