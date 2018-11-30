package com.blogspot.teperi31.moneydiary;

import java.util.HashMap;
import java.util.Map;

/*
* 가계부 파이어베이스 데이터 형식 및 넣는 값 지정
* */

public class DataMoneyFlowFB {
	
	
	public String type;
	public long date;
	public String account;
	public String category;
	public long price;
	public String usage;
	
	public DataMoneyFlowFB() {
		// Default constructor required for calls to DataSnapshot.getValue(DataMoneyFlowFB.class)
	}
	
	public DataMoneyFlowFB(String type, Long date, String account, String category, Long price, String usage) {
		this.type = type;
		this.date = date;
		this.account = account;
		this.category = category;
		this.price = price;
		this.usage = usage;
	}
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("type", type);
		result.put("date", date);
		result.put("account", account);
		result.put("category", category);
		result.put("price", price);
		result.put("usage", usage);
		
		return result;
	}
}
