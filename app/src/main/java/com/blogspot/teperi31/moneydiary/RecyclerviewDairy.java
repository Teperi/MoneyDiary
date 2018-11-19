package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class RecyclerviewDairy extends AppCompatActivity {
	private RecyclerView mRecyclerView;
	private RecyclerView.LayoutManager mLayoutManager;
	ArrayList<DataDairy> dList;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_dairy);
		
		// 액션 바 삽입
		// 추후 Dairy 전용 toolbar 생성
		Toolbar myToolbar = findViewById(R.id.toolbarTop_List_moneyflow);
		setSupportActionBar(myToolbar);
		
		mRecyclerView = findViewById(R.id.dairy_recycler_view);
		
		// 리니어 레이아웃 매니저. 한줄씩 쌓임
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		// 데이터 입력
		dList = new ArrayList<>();
		
		dList.add(new DataDairy(2018, 10, 25, "월급날이다!", "신나는 월급", 0));
		dList.add(new DataDairy(2018, 10, 26, "월급날이다!", "신나는 월급", R.drawable.cat3));
		
		AdapterDRecycler myAdapter = new AdapterDRecycler(dList);
		
		mRecyclerView.setAdapter(myAdapter);
		
		
	}
}
