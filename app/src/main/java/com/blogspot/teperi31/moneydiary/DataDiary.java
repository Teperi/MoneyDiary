package com.blogspot.teperi31.moneydiary;

import android.net.Uri;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DataDiary implements Serializable {
	
	Calendar DListDate;
	String DListDateString;
	String DListTitle;
	String DListContent;
	Uri DListImage;
	
	public DataDiary(Calendar date, String title, String content, Uri image){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		this.DListDate = date;
		this.DListTitle = title;
		this.DListContent = content;
		this.DListImage = image;
		this.DListDateString = sdf.format(DListDate.getTime());
		
	}
	
}
