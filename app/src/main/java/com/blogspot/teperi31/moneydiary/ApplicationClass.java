package com.blogspot.teperi31.moneydiary;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ApplicationClass extends Application {
	// 데이터 Json 변환을 위한 변수
	static Gson gson;
	
	// 데이터를 1차적으로 Map 에 저장
	static HashMap<Integer, DataDiary> dListMap;
	
	// 데이터를 가져와 실제 사용할 ArrayList
	static ArrayList<DataMoneyFlow> mfList;
	static ArrayList<DataDiary> dList;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		mfList = new ArrayList<>();
		// new Calendar 의 경우 추상 클래스여서 인스턴스화 시킬 수 없다고 오류가 나옴
		// Gregorian Calendar 로 해결함
		
		// Gregorian Calendar 의 경우 월이 0부터 시작함 ( 0 -> 1월 / 10 -> 11월 ...)
		mfList.add(new DataMoneyFlow("입금", new GregorianCalendar(2018, 9, 25).getTime(), "현금", "월급", 200000, "월급"));
		mfList.add(new DataMoneyFlow("이체", new GregorianCalendar(2018, 9, 25).getTime(), "체크카드", "이체", 150000, "월급이체"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "점심", 6000, "백채김치찌개"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "회식", 10000, "한신포차"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 27).getTime(), "현금", "점심", 5000, "할매순대국"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 9, 27).getTime(), "체크카드", "저녁", 10000, "국물떡볶이"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 1).getTime(), "현금", "저녁", 7000, "피자스쿨"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 2).getTime(), "현금", "간식", 7000, "GS25"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018, 10, 2).getTime(), "체크카드", "영화", 10000, "CGV"));
		
		/*SharedPreference*/
		
		dList = new ArrayList<>();
			dList.add(new DataDiary(new GregorianCalendar(2018, 9, 26).getTime(), "sss", "신나는 월급", null, 0));
			dList.add(new DataDiary(new GregorianCalendar(2018, 9, 27).getTime(), "tt", "이 고양이 사진이 최고인거 같다.", (String) ("android.resource://com.blogspot.teperi31.moneydiary/drawable/" + R.drawable.cat3),1));
		
		gson = new Gson();
		
		dListMap = new HashMap<>();
		
		for (int i = 0; i <dList.size(); i++){
			dListMap.put(i, dList.get(i));
		}
		
		
		
		SharedPreferences saveDiary = getSharedPreferences("Diary",MODE_PRIVATE);
		SharedPreferences.Editor save = saveDiary.edit();
		
		save.putString("Diary",gson.toJson(dListMap));
		save.apply();
		save.remove("Diary");
		
		
		dList = new ArrayList<>();
		
		dListMap = new HashMap<>();
		System.out.println(dList.size());
		System.out.println(dListMap.size());
		
		String json = saveDiary.getString("Diary",null);
		System.out.println(json);
		Type collectionType = new TypeToken<HashMap<Integer,DataDiary>>(){}.getType();
		
		dListMap = gson.fromJson(json , collectionType);


		System.out.println(dListMap.get(0).DListContent);
		
		dList.add(dListMap.get(0));
//		System.out.println(dListMap.get(1).DListContent);
		
		
		
		
		/*HashMap<Integer, DataDiary> hashdList = new HashMap<>();
		
		if (hashdList.size() == 0) {
			dList = new ArrayList<>();
			*//*dList.add(new DataDiary(new GregorianCalendar(2018, 9, 25).getTime(), 0,"월급날이다!", "신나는 월급", null, 0));
			dList.add(new DataDiary(new GregorianCalendar(2018, 9, 26).getTime(), 0,"이쁜 고양이", "이 고양이 사진이 최고인거 같다.", Uri.parse("android.resource://com.blogspot.teperi31.moneydiary/drawable/" + R.drawable.cat3),1));
			*//*
		} else {
			dList = new ArrayList<>();
			for (int i = 0; i < hashdList.size(); i++) {
				dList.add(i, hashdList.get(i));
			}
		}*/
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
