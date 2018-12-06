package com.blogspot.teperi31.moneydiary;

import java.util.Map;

/*
* 메신저를 사용하는 USER 들의 채팅방 정보
* 추후 이 데이터를 사용해서 메신저 메인 방으로 노출 예정
*
* */

public class DataMessengerUserRoom {
	public String title;
	public String RoomType;
	public String location;
	public Long UserCount;
	public Map<String, Object> UserList;
	
	public DataMessengerUserRoom() {
	
	}
	
}
