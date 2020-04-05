package app;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

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
    private Vector<String> fileNames;

    public Message(String sender, String message, Date date, String[] sports, String[] views, Vector<String> fileNames) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.sports = sports;
        this.views = views;
        this.fileNames = fileNames;
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

	public Vector<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(Vector<String> fileNames) {
		this.fileNames = fileNames;
	}
}