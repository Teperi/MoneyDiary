package com.blogspot.teperi31.moneydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/*
 * 파이어베이스와 연동한 리사이클러뷰 만들기
 *
 * */
public class RecyclerViewMoneyFlowFB extends AppCompatActivity {
	
	Toolbar mToolbar;
	
	private DatabaseReference mDatabase;
	private FirebaseUser user;
	
	private FirebaseRecyclerAdapter<DataMoneyFlowFB, ViewHolderMoneyFlow> mAdapter;
	private RecyclerView mRecycler;
	private LinearLayoutManager mLayoutManager;
	
	/*
	 * 뷰 생성 시
	 * 액션바 생성
	 * 데이터베이스 연결
	 * 리사이클러뷰 연결 & 사이즈 고정
	 * 어댑터 만들기
	 * */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recyclerview_moneyflow);
		
		// 액션바
		mToolbar = findViewById(R.id.toolbarTop_List_moneyflow);
		setSupportActionBar(mToolbar);
		// DB 연결
		mDatabase = FirebaseDatabase.getInstance().getReference();
		user = FirebaseAuth.getInstance().getCurrentUser();
		// 리사이클러뷰 연결 & 고정
		mRecycler = findViewById(R.id.moneyflow_recycler_view);
		mRecycler.setHasFixedSize(true);
		
		// 세로로 쌓기 기능
		mLayoutManager = new LinearLayoutManager(this);
		mRecycler.setLayoutManager(mLayoutManager);
		
		// 가져올 데이터 쿼리 정렬
		//TODO : 날짜별 정렬 기능 넣어야 함
		Query MoneyFlowQuery = mDatabase.child("moneyflow").child(user.getUid()).limitToLast(100);
		// 어뎁터 만들기
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMoneyFlowFB>()
				.setQuery(MoneyFlowQuery, DataMoneyFlowFB.class)
				.build();
		
		mAdapter = new FirebaseRecyclerAdapter<DataMoneyFlowFB, ViewHolderMoneyFlow>(options) {
			
			@Override
			public ViewHolderMoneyFlow onCreateViewHolder(ViewGroup viewGroup, int i) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerviewrow_moneyflow, viewGroup, false);
				return new ViewHolderMoneyFlow(itemView);
			}
			
			@Override
			protected void onBindViewHolder(@NonNull ViewHolderMoneyFlow holder, int position, @NonNull DataMoneyFlowFB model) {
				holder.bindToMoneyFlow(model);
				
				DatabaseReference MFref = getRef(position);
				final String MFKey = MFref.getKey();
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), EditMoneyFlowDataFB.class);
						intent.putExtra(EditMoneyFlowDataFB.EXTRA_MFDATA_KEY, MFKey);
						startActivity(intent);
					}
				});
			}
			
		};
		mRecycler.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(mAdapter != null) {
			mAdapter.startListening();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mAdapter != null) {
			mAdapter.stopListening();
		}
	}
	
	//	toolbar 에 메뉴 띄워주는 함수
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions_moneyflow, menu);
		return true;
	}
	
	//	메뉴 버튼 클릭시 나올 상황 입력
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionAdd:
				Intent i = new Intent(RecyclerViewMoneyFlowFB.this, InputMoneyFlowCreateFB.class);
				startActivity(i);
				Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.actionmyblog:
				Toast.makeText(this, "개발자 블로그로 연결합니다.", Toast.LENGTH_SHORT).show();
				Intent actionBlogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://naver.com"));
				startActivity(actionBlogIntent);
				return true;
			
			case R.id.actionmyemail:
				Toast.makeText(this, "개발자에게 메일을 보냅니다.", Toast.LENGTH_SHORT).show();
				Intent actionEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:teperi31@gmail.com"));
				startActivity(actionEmailIntent);
				return true;
			
			case R.id.actionmyphone:
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
}
