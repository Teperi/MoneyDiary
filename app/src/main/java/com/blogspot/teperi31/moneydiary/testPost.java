package com.blogspot.teperi31.moneydiary;


import java.util.HashMap;
import java.util.Map;

public class testPost {
	
	public String uid;
	public String author;
	public String title;
	public String content;
	
	public testPost() {}
	
	public testPost(String uid, String author, String title, String content) {
		this.uid = uid;
		this.author = author;
		this.title = title;
		this.content = content;
	}
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("uid", uid);
		result.put("author", author);
		result.put("title", title);
		result.put("content", content);
		
		return result;
	}
}
