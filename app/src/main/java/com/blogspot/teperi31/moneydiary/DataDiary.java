package com.blogspot.teperi31.moneydiary;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/*
* 다이어리 부분의 데이터를 저장하는 Class
* */

public class DataDiary {
	public String uid;
	int DListId;
	Date DListDate;
	String DListDateString;
	String DListTitle;
	String DListContent;
	String DListImageUri;
	
	public DataDiary(Date date, String title, String content, String imageuri, int id) {
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		this.DListDate = date;
		this.DListTitle = title;
		this.DListContent = content;
		this.DListImageUri = imageuri;
		this.DListDateString = sdf.format(DListDate.getTime());
		this.DListId = id;
	}
	
	public static Comparator<DataDiary> DiaryDateComparator = new Comparator<DataDiary>() {
		@Override
		public int compare(DataDiary o1, DataDiary o2) {
			return o2.DListDate.compareTo(o1.DListDate);
		}
	};
}
