package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

public class EditNote{

	@SerializedName("data")
	private Note data;

	@SerializedName("success")
	private boolean success;

	public void setData(Note data){
		this.data = data;
	}

	public Note getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}
}