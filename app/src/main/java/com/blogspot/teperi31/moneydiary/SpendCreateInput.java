package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ArrayAdapter;
import android.widget.Toolbar;

import java.util.ArrayList;

public class SpendCreateInput extends AppCompatActivity {
	
	AppCompatSpinner sp_year;
	AppCompatSpinner sp_month;
	AppCompatSpinner sp_day;
	ArrayList<Integer> yearselect;
	ArrayList<Integer> monthselect;
	ArrayList<Integer> dayselect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatespend);
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_main);
		setActionBar(myToolbar);
		
		// 년도 스피너
		sp_year = findViewById(R.id.inputCreateSpendSubclassDateYearSpinner);
		
		yearselect = new ArrayList<Integer>();
		yearselect.add(2018);
		
		ArrayAdapter yearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearselect);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_year.setAdapter(yearAdapter);
		
		// 월 스피너
		sp_month = findViewById(R.id.inputCreateSpendSubclassDateMonthSpinner);
		
		monthselect = new ArrayList<Integer>();
		monthselect.add(10);
		monthselect.add(11);
		monthselect.add(12);
		
		ArrayAdapter monthAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, monthselect);
		monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_month.setAdapter(monthAdapter);
		
		// 일 스피너
		sp_day = findViewById(R.id.inputCreateSpendSubclassDateDaySpinner);
		
		dayselect = new ArrayList<Integer>();
		dayselect.add(1);
		dayselect.add(2);
		dayselect.add(3);
		dayselect.add(4);
		dayselect.add(5);
		
		ArrayAdapter dayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dayselect);
		dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_day.setAdapter(dayAdapter);
		
		
		
		
	}
}