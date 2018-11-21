package com.blogspot.teperi31.moneydiary;

import android.app.Application;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ApplicationClass extends Application {
	
	static ArrayList<DataMoneyFlow> mfList;
	static ArrayList<DataDairy> dList;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mfList = new ArrayList<>();
		// new Calendar 의 경우 추상 클래스여서 인스턴스화 시킬 수 없다고 오류가 나옴
		// Gregorian Calendar 로 해결함
		
		// Gregorian Calendar 의 경우 월이 0부터 시작함 ( 0 -> 1월 / 10 -> 11월 ...)
		
		mfList.add(new DataMoneyFlow("입금", new GregorianCalendar(2018,9,25) , "현금", "월급", 200000, "월급"));
		mfList.add(new DataMoneyFlow("이체", new GregorianCalendar(2018,9,25) , "체크카드", "이체", 150000, "월급이체"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,9,26), "체크카드", "점심", 6000, "백채김치찌개"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,9,26), "체크카드", "회식", 10000, "한신포차"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,9,27), "현금", "점심", 5000, "할매순대국"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,9,27), "체크카드", "저녁", 10000, "국물떡볶이"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,10,1), "현금", "저녁", 7000, "피자스쿨"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,10,2), "현금", "간식", 7000, "GS25"));
		mfList.add(new DataMoneyFlow("출금", new GregorianCalendar(2018,10,2), "체크카드", "영화", 10000, "CGV"));
		
		dList = new ArrayList<>();
		
		dList.add(new DataDairy(new GregorianCalendar(2018,9,25), "월급날이다!", "신나는 월급", 0));
		dList.add(new DataDairy(new GregorianCalendar(2018,9,26), "이쁜 고양이", "이 고양이 사진이 최고인거 같다.", R.drawable.cat3));
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
