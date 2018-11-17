package com.blogspot.teperi31.moneydiary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

//import java.util.ArrayList;
//import java.util.List;

public class Recyclerviewtest extends AppCompatActivity {
	
	
	
	private RecyclerView mRecyclerView;
	private RecyclerView.LayoutManager mLayoutManager;
	ArrayList<MoneyDiaryList> mdList;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerviewtest);
		
		// 액션 바 삽입
		Toolbar myToolbar = findViewById(R.id.toolbarTop_List);
		setSupportActionBar(myToolbar);
		
		
		
		
		mRecyclerView = findViewById(R.id.recycler_view);
		
		// 사이즈 고정, 리사이클러 뷰에서 content 사이즈를 바꾸지 말라?
		mRecyclerView.setHasFixedSize(true);
		
		// 리니어 레이아웃 매니저. 한줄씩 쌓임
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		
		// 데이터 입력
		mdList = new ArrayList<>();
		mdList.add(new MoneyDiaryList("0", "입금" ,2018, 10, 25, "현금", "월급", 200000, "월급"));
		mdList.add(new MoneyDiaryList("1", "이체" ,2018, 10, 25, "체크카드", "이체", 150000, "월급이체"));
		mdList.add(new MoneyDiaryList("2", "출금" ,2018, 10, 26, "체크카드", "점심", 6000, "백채김치찌개"));
		mdList.add(new MoneyDiaryList("3", "출금" ,2018, 10, 26, "체크카드", "회식", 10000, "한신포차"));
		mdList.add(new MoneyDiaryList("4", "출금" ,2018, 10, 27, "현금", "점심", 5000, "할매순대국"));
		mdList.add(new MoneyDiaryList("5", "출금" ,2018, 11, 1, "체크카드", "회식", 10000, "국물떡볶이"));
		mdList.add(new MoneyDiaryList("6", "출금" ,2018, 11, 1, "현금", "저녁", 7000, "피자스쿨"));
		mdList.add(new MoneyDiaryList("7", "출금" ,2018, 11, 2, "현금", "간식", 7000, "GS25"));
		mdList.add(new MoneyDiaryList("8", "출금" ,2018, 11, 2, "체크카드", "영화", 10000, "CGV"));
		
		//내 어댑터와 데이터 연결
		MyAdapter myAdapter = new MyAdapter(mdList);
		
		mRecyclerView.setAdapter(myAdapter);
		
		

//
	}
}
