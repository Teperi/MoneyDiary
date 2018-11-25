package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PreferenceUtil {
	
	static Gson gson;
	static SharedPreferences pref;
	
	static SharedPreferences.Editor savePref;
	static Type collectionTypeDiary = new TypeToken<HashMap<Integer,DataDiary>>(){}.getType();
	
	
	public static void setDiary(Context context) {
		// id 값과 Position 값을 맞추기 위한 정렬 : Date 기준
		Collections.sort(ApplicationClass.dList, DataDiary.DateComparator);
		
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
	
	public static void getDiary() {
		String json = getPreferences("Diary");
		
		gson = new Gson();
		ApplicationClass.dList = new ArrayList<>();
		ApplicationClass.dListMap = new HashMap<>();
		
		ApplicationClass.dListMap = gson.fromJson(json, collectionTypeDiary);
		
		for(int i = 0; i < ApplicationClass.dListMap.size(); i++){
			ApplicationClass.dList.add(ApplicationClass.dListMap.get(i));
		}
	}
	
	public static void setPreferences(Context context, String key, String value) {
		pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		savePref = pref.edit();
		savePref.putString(key, value);
		savePref.apply();
	}
	
	public static String getPreferences(String key) {
		return pref.getString(key, null);
	}
	
	
}
