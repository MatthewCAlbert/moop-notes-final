package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

public class Login{

	@SerializedName("expiresIn")
	private String expiresIn;

	@SerializedName("success")
	private boolean success;

	@SerializedName("data")
	private User user;

	@SerializedName("token")
	private String token;

	public void setExpiresIn(String expiresIn){
		this.expiresIn = expiresIn;
	}

	public String getExpiresIn(){
		return expiresIn;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}