package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

public class AuthCheck{

	@SerializedName("data")
	private User data;

	@SerializedName("success")
	private boolean success;

	public void setData(User data){
		this.data = data;
	}

	public User getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}
}