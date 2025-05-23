package com.sms.SchoolManagementBrahmada.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class LoginUser {

	
	@NotEmpty
	@Email
	private String email;
	
	@NotEmpty
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
