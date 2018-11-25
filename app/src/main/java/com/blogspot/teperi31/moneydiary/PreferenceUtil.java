package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;

public class PreferenceUtil {
	
	Gson gson;
	
	

	
	
	
	public void saveDiary(Context context) {
		// id 값과 Position 값을 맞추기 위한 정렬 : Date 기준
		Collections.sort(ApplicationClass.dList, DataDiary.DateComparator);
		
		//id 값을 Position 값으로 변경
		for(int i = 0; i < ApplicationClass.dList.size(); i++) {
			ApplicationClass.dList.get(i).DListId = i;
		}
		
		ApplicationClass.dListMap = new HashMap<>();
		
		// 정렬된 값을 Map으로 전달
		for (int i = 0; i < ApplicationClass.dList.size(); i++) {
			ApplicationClass.dListMap.put(i,ApplicationClass.dList.get(i));
		}
		
		// Map 값을 Json으로 변환
		
		gson = new Gson();
		putSharedPreference(context, "Diary", gson.toJson(ApplicationClass.dListMap));
		
	}
	
	public static void putSharedPreference
			(Context context, String key, String value)
	{
		SharedPreferences prefs =
				PreferenceManager.getDefaultSharedPreferences(context);
		
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString(key, value);
		editor.apply();
	}
}
