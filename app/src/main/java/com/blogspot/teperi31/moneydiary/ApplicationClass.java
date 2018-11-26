package com.blogspot.teperi31.moneydiary;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;

public class ApplicationClass extends Application {
	
	// 데이터를 1차적으로 Map 에 저장
	static HashMap<Integer, DataDiary> dListMap;
	static HashMap<Integer, DataMoneyFlow> mfListMap;
	static HashSet<String> mfAccountSet;
	static HashSet<String> mfExpenseCategorySet;
	static HashSet<String> mfIncomeCategorySet;
	
	// 데이터를 가져와 실제 사용할 ArrayList
	static ArrayList<DataMoneyFlow> mfList;
	static ArrayList<DataDiary> dList;
	static ArrayList<String> mfAccountList;
	static ArrayList<String> mfExpenseCategoryList;
	static ArrayList<String> mfIncomeCategoryList;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		/*
		
//		자료를 넣어줄 필요성이 있는 경우 여기서부터 시작함

		mfList = new ArrayList<>();
		// new Calendar 의 경우 추상 클래스여서 인스턴스화 시킬 수 없다고 오류가 나옴
		// Gregorian Calendar 로 해결함

		// Gregorian Calendar 의 경우 월이 0부터 시작함 ( 0 -> 1월 / 10 -> 11월 ...)
		mfList.add(new DataMoneyFlow("수입", new GregorianCalendar(2018, 9, 25).getTime(), "현금", "월급", 200000, "월급",0));
		mfList.add(new DataMoneyFlow("이체", new GregorianCalendar(2018, 9, 25).getTime(), "체크카드", "이체", 150000, "월급이체",1));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "점심", 6000, "백채김치찌개",2));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 9, 26).getTime(), "체크카드", "회식", 10000, "한신포차",3));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 9, 27).getTime(), "현금", "점심", 5000, "할매순대국",4));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 9, 27).getTime(), "체크카드", "저녁", 10000, "국물떡볶이",5));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 10, 1).getTime(), "현금", "저녁", 7000, "피자스쿨",6));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 10, 2).getTime(), "현금", "간식", 7000, "GS25",7));
		mfList.add(new DataMoneyFlow("지출", new GregorianCalendar(2018, 10, 2).getTime(), "체크카드", "영화", 10000, "CGV",8));
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
		
		mfAccountSet = new HashSet<>();
		mfExpenseCategorySet = new HashSet<>();
		
		mfAccountList = new ArrayList<>();
		mfExpenseCategoryList = new ArrayList<>();
		
		mfIncomeCategorySet = new HashSet<>();
		mfIncomeCategoryList = new ArrayList<>();
		
		
		mfAccountSet.add("현금");
		mfAccountSet.add("체크카드");
		
		mfAccountList.addAll(mfAccountSet);
		
		mfExpenseCategorySet.add("점심");
		mfExpenseCategorySet.add("저녁");
		mfExpenseCategorySet.add("간식");
		mfExpenseCategorySet.add("회식");
		mfExpenseCategorySet.add("영화");
		mfExpenseCategorySet.add("기타");
		
		mfExpenseCategoryList.addAll(mfExpenseCategorySet);
		
		mfIncomeCategorySet.add("월급");
		mfIncomeCategorySet.add("기타");
		
		mfIncomeCategoryList.addAll(mfIncomeCategorySet);
		
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
