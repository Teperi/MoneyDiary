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
