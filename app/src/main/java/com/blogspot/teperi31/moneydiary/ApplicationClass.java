package com.blogspot.teperi31.moneydiary;

import android.app.Application;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationClass extends Application {
	
	// 데이터를 1차적으로 Map 에 저장
	static HashMap<Integer, DataDiary> dListMap;
	static HashMap<Integer, DataMoneyFlow> mfListMap;
	
	// 데이터를 가져와 실제 사용할 ArrayList
	static ArrayList<DataMoneyFlow> mfList;
	static ArrayList<DataDiary> dList;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		/*자료를 넣어줄 필요성이 있는 경우 여기서부터 시작함
		
		mfList = new ArrayList<>();
		// new Calendar 의 경우 추상 클래스여서 인스턴스화 시킬 수 없다고 오류가 나옴
		// Gregorian Calendar 로 해결함

		// Gregorian Calendar 의 경우 월이 0부터 시작함 ( 0 -> 1월 / 10 -> 11월 ...)
		mfList.add(new DataMoneyFlow("입금", new GregorianCalendar(2018, 9, 25).getTime(), "현금", "월급", 200000, "월급",0));
		mfList.add(new DataMoneyFlow("이체", new GregorianCalendar(2018, 9, 25).getTime(), "체크카드", "이체", 150000, "월급이체",1));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "점심", 6000, "백채김치찌개",2));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "회식", 10000, "한신포차",3));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 27).getTime(), "현금", "점심", 5000, "할매순대국",4));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 27).getTime(), "체크카드", "저녁", 10000, "국물떡볶이",5));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 1).getTime(), "현금", "저녁", 7000, "피자스쿨",6));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 2).getTime(), "현금", "간식", 7000, "GS25",7));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 2).getTime(), "체크카드", "영화", 10000, "CGV",8));
		dList = new ArrayList<>();

		dList.add(new DataDiary(new GregorianCalendar(2018, 10, 2).getTime(), "제목", "이름", null, 0));
		dList.add(new DataDiary(new GregorianCalendar(2018, 10, 3).getTime(), "제목", "이름", null, 1));
		dList.add(new DataDiary(new GregorianCalendar(2018, 10, 4).getTime(), "제목", "이름", null, 2));
		dList.add(new DataDiary(new GregorianCalendar(2018, 10, 5).getTime(), "제목", "이름", null, 3));

		Context context = this;
		
		UtilPreference.setDiary(this);
		UtilPreference.setMoneyflow(this);*/
		
		
		UtilPreference.getDiary(this);
		
		UtilPreference.getMoneyflow(this);
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
