package com.blogspot.teperi31.moneydiary;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/*
* 기본적인 다이어리 데이터 정렬 방식
*
* DListDate 값으로 비교한 후
* 값이 같은게 있다면 DListOrder 를 통해 순서 정함
*
* 늦게 들어온 값이 Order 가 높아질 수 밖에 없음
*
* DListId 의 경우 데이터가 들어온 순서대로 값이 지정되는 구조
* */


public class DataDiary {
	int DListId;
	Date DListDate;
	String DListDateString;
	String DListTitle;
	String DListContent;
	String DListImageUri;
	
	public DataDiary(Date date, String title, String content, String imageuri, int id){
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
		this.DListDate = date;
		this.DListTitle = title;
		this.DListContent = content;
		this.DListImageUri = imageuri;
		this.DListDateString = sdf.format(DListDate.getTime());
		this.DListId = id;
	}
	
	public static Comparator<DataDiary> DateComparator = new Comparator<DataDiary>() {
		@Override
		public int compare(DataDiary o1, DataDiary o2) {
			return o2.DListDate.compareTo(o1.DListDate);
		}
	};
}
