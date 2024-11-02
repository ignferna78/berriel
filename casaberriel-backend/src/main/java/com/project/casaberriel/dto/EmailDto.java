package com.project.casaberriel.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EmailDto {

	@NotBlank(message = "Name is mandatory")
	private String name;
	
	@NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
	
	 @NotBlank(message = "Message is mandatory")
    private String message;
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
    
}
