package com.blogspot.teperi31.moneydiary;

import android.net.Uri;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DataDairy implements Serializable {
	
	Calendar DListDate;
	String DListDateString;
	String DListTitle;
	String DListContent;
	int DListImage;
	
	public DataDairy(Calendar date, String title, String content, int image){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		
		this.DListDate = date;
		this.DListTitle = title;
		this.DListContent = content;
		this.DListImage = image;
		this.DListDateString = sdf.format(DListDate.getTime());
		
	}
	
}
