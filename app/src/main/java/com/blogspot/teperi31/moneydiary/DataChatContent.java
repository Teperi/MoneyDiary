package com.blogspot.teperi31.moneydiary;

import java.util.HashMap;
import java.util.Map;

// 메시지 데이터를 담는 데이터 형식
public class DataChatContent {
	
	private String Uid;
	private String text;
	private String name;
	private String photoUrl;
	private String imageUrl;
	private Long DateTime;
	public Map<String,Object> ReadUsers;
	public Long UnReadUserCount;
	public String messageKey;
	
	// firebase 받아오는 곳
	public DataChatContent() {
	}
	
	// send 시 집어넣기
	public DataChatContent(String Uid, String text, String name, String photoUrl, String imageUrl, Long DateTime, Long TotalUsers, String messageKey) {
		this.Uid = Uid;
		this.text = text;
		this.name = name;
		this.photoUrl = photoUrl;
		this.imageUrl = imageUrl;
		this.DateTime = DateTime;
		this.ReadUsers = new HashMap<>();
		this.ReadUsers.put(Uid,true);
		this.UnReadUserCount = TotalUsers - 1;
		this.messageKey = messageKey;
	}
	
	public String getId() {
		return Uid;
	}
	
	public void setId(String id) {
		this.Uid = id;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhotoUrl() {
		return photoUrl;
	}
	
	public String getText() {
		return text;
	}
	
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public Long getDateTime() {return DateTime;}
	
	public void setDateTime(Long DateTime) {this.DateTime = DateTime;}
}
