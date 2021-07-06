package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

public class DeleteNote{

	@SerializedName("success")
	private boolean success;

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}
}