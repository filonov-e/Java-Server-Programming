package app;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

public class Feedback implements Serializable {
	private static final long serialVersionUID = 1L;

	private String firstname;
	private String surname;
	private String email;
	private String phone;
	private String feedback;

	public Feedback(String firstname, String surname, String email, String phone, String feedback) {
		this.firstname = firstname;
		this.surname = surname;
		this.email = email;
		this.phone = phone;
		this.feedback = feedback;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
}