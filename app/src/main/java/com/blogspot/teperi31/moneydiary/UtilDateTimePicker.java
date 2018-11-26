package com.blogspot.teperi31.moneydiary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilDateTimePicker {
	
	static Calendar myCalendar;
	static TextView datepicker;
	static DatePickerDialog.OnDateSetListener date;
	
	public static Date setTodayDate(View datetextid){
		// 날짜 설정
		myCalendar = Calendar.getInstance(Locale.KOREA);
		// 텍스트뷰에 있는 날짜로 선택
		datepicker = (TextView) datetextid;
		// 현재 날짜로 선택
		updateLabel();
		
		return myCalendar.getTime();
	}
	
	
	public static Date setDatepopup(final Context context, View datetextid){
		
		// 날짜 설정
		myCalendar = Calendar.getInstance(Locale.KOREA);
		// 텍스트뷰에 있는 날짜로 선택
		datepicker = (TextView) datetextid;
		
		// 날짜를 변경해주는 값 받아주는 변수
		date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				myCalendar.set(year, month, dayOfMonth);
				updateLabel();
			}
		};
		
		// 온클릭시 팝업 띄우기
		datepicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		return myCalendar.getTime();
	}
	
	
	private static void updateLabel(){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		datepicker.setText(sdf.format(myCalendar.getTime()));
	}
}
