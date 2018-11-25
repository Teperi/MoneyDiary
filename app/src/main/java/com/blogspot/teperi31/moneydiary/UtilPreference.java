package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/*
*
* SharedPreferences 저장
* SharedPreferences 가져오기
* ArraryList 내 정렬 기능
* 처리하기 위한 클래스
*
* set 에서 정렬 및 저장하고
* get 에서 저장된 것을 가져온다.
*
* */


public class UtilPreference {
	
	static Gson gson;
	static SharedPreferences pref;
	
	static SharedPreferences.Editor savePref;
	static Type collectionTypeDiary = new TypeToken<HashMap<Integer,DataDiary>>(){}.getType();
	static Type collectionTypeMoneyFlow = new TypeToken<HashMap<Integer,DataMoneyFlow>>(){}.getType();
	
	
	public static void setDiary(Context context) {
		// id 값과 Position 값을 맞추기 위한 정렬 : Date 기준
		Collections.sort(ApplicationClass.dList, DataDiary.DiaryDateComparator);
		
		//id 값을 Position 값으로 변경
		for (int i = 0; i < ApplicationClass.dList.size(); i++) {
			ApplicationClass.dList.get(i).DListId = i;
		}
		
		ApplicationClass.dListMap = new HashMap<>();
		
		// 정렬된 값을 Map으로 전달
		for (int i = 0; i < ApplicationClass.dList.size(); i++) {
			ApplicationClass.dListMap.put(i, ApplicationClass.dList.get(i));
		}
		
		// Map 값을 Json으로 변환
		gson = new Gson();
		
		setPreferences(context, "Diary", gson.toJson(ApplicationClass.dListMap));
	}
	
	
	public static void getDiary(Context context) {
		String json = getPreferences(context, "Diary");
		
		gson = new Gson();
		ApplicationClass.dList = new ArrayList<>();
		ApplicationClass.dListMap = new HashMap<>();
		
		if(json.equals("notinValue")){
		
		} else {
			ApplicationClass.dListMap = gson.fromJson(json, collectionTypeDiary);
			
			for(int i = 0; i < ApplicationClass.dListMap.size(); i++){
				ApplicationClass.dList.add(ApplicationClass.dListMap.get(i));
			}
		}
		
		
	}
	
	
	
	public static void setMoneyflow(Context context) {
		// id 값과 Position 값을 맞추기 위한 정렬 : Date 기준
		Collections.sort(ApplicationClass.mfList, DataMoneyFlow.MoneyFlowDateComparator);
		
		//id 값을 Position 값으로 변경
		for (int i = 0; i < ApplicationClass.mfList.size(); i++) {
			ApplicationClass.mfList.get(i).MFListId = i;
		}
		
		ApplicationClass.mfListMap = new HashMap<>();
		
		// 정렬된 값을 Map으로 전달
		for (int i = 0; i < ApplicationClass.mfList.size(); i++) {
			ApplicationClass.mfListMap.put(i, ApplicationClass.mfList.get(i));
		}
		
		// Map 값을 Json으로 변환
		gson = new Gson();
		
		setPreferences(context, "MoneyFlow", gson.toJson(ApplicationClass.mfListMap));
	}
	
	public static void getMoneyflow(Context context) {
		String json = getPreferences(context,"MoneyFlow");
		
		gson = new Gson();
		ApplicationClass.mfList = new ArrayList<>();
		ApplicationClass.mfListMap = new HashMap<>();
		
		
		
		if(json.equals("notinValue")){
		
		} else {
			ApplicationClass.mfListMap = gson.fromJson(json, collectionTypeMoneyFlow);
			
			for(int i = 0; i < ApplicationClass.mfListMap.size(); i++){
				ApplicationClass.mfList.add(ApplicationClass.mfListMap.get(i));
			}
		}
		
		
	}
	
	
	
	
	
	public static void setPreferences(Context context, String key, String value) {
		pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		savePref = pref.edit();
		savePref.putString(key, value);
		savePref.apply();
	}
	
	public static String getPreferences(Context context, String key) {
		pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		return pref.getString(key, "notinValue");
	}
	
	
}
