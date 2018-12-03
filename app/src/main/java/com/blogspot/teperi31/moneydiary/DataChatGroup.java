package com.blogspot.teperi31.moneydiary;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DataChatGroup {
	public String groupid;
	public boolean groupparticipation;
	
	public DataChatGroup() {
		// Default constructor required for calls to DataSnapshot.getValue(DataChatGroup.class)
	}
	
	public DataChatGroup(String groupid, boolean participation) {
		this.groupid = groupid;
		this.groupparticipation = participation;
	}
	
	@Exclude
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put(groupid, groupparticipation);
		
		return result;
	}
	
}
