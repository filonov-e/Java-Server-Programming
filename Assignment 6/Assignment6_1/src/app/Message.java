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
    private String date;
    private String fileName;

    public Message(String sender, String message, String date, String fileNames) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.fileName = fileNames;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}