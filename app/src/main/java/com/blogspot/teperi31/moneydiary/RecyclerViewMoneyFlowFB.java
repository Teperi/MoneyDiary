package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * 파이어베이스와 연동한 리사이클러뷰 만들기
 *
 * */
public class RecyclerViewMoneyFlowFB extends AppCompatActivity implements View.OnClickListener {
	
	Toolbar mToolbar;
	
	private DatabaseReference mDatabase;
	private FirebaseUser user;
	
	private FirebaseRecyclerAdapter<DataMoneyFlowFB, ViewHolderMoneyFlow> mAdapter;
	private RecyclerView mRecycler;
	private LinearLayoutManager mLayoutManager;
	
	// 액션모드 선택을 위한 저장 데이터
	private Map<String, String> selectedItems = new HashMap<>();
	
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
		setContentView(R.layout.moneyflow_list);
		
		findViewById(R.id.moneyflow_list_fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation animation = AnimationUtils.loadAnimation(RecyclerViewMoneyFlowFB.this, R.anim.scale_up);
				animation.start();
			}
		});
		
		
		// 액션바
		mToolbar = findViewById(R.id.moneyflow_list_toolbarTop);
		setSupportActionBar(mToolbar);
		// 네비게이션바
		findViewById(R.id.moneyflow_list_bottomBar_dashboardicon).setOnClickListener(this);
		findViewById(R.id.moneyflow_list_bottomBar_messengericon).setOnClickListener(this);
		findViewById(R.id.moneyflow_list_bottomBar_myinfoicon).setOnClickListener(this);
		((ImageButton) findViewById(R.id.moneyflow_list_bottomBar_listicon)).setImageResource(R.drawable.ic_action_list_clicked);
		
		
		// DB 연결
		mDatabase = FirebaseDatabase.getInstance().getReference();
		mDatabase.keepSynced(true);
		user = FirebaseAuth.getInstance().getCurrentUser();
		// 리사이클러뷰 연결 & 고정
		mRecycler = findViewById(R.id.moneyflow_list_recyclerview);
		mRecycler.setHasFixedSize(true);
		
		// 세로로 쌓기 기능
		mLayoutManager = new LinearLayoutManager(this);
		// Query 에 있는 정렬 기능을 역순으로 해 주는 부분
		mLayoutManager.setReverseLayout(true);
		mLayoutManager.setStackFromEnd(true);
		
		mRecycler.setLayoutManager(mLayoutManager);
		
		
		// 가져올 데이터 쿼리 정렬 - 날짜 데이터 기준 정렬
		Query MoneyFlowQuery = mDatabase.child("moneyflow").child(user.getUid()).orderByChild("date");
		// 데이터 가져오는 builder 설정
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<DataMoneyFlowFB>()
				.setQuery(MoneyFlowQuery, DataMoneyFlowFB.class)
				.build();
		// 리사리클러 어뎁터 설정
		// 어뎁터 클래스를 따로 만들어보다가 에러나서 여기서 새로 만들기로 결정함
		mAdapter = new FirebaseRecyclerAdapter<DataMoneyFlowFB, ViewHolderMoneyFlow>(options) {
			// ViewHolder 만들기
			@Override
			public ViewHolderMoneyFlow onCreateViewHolder(ViewGroup viewGroup, int i) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moneyflow_list_row, viewGroup, false);
				return new ViewHolderMoneyFlow(itemView);
			}
			
			// 묶어주기
			@Override
			protected void onBindViewHolder(@NonNull ViewHolderMoneyFlow holder, int position, @NonNull DataMoneyFlowFB model) {
				holder.bindToMoneyFlow(model);
				
				
				// 키값 저장해서 에디트 페이지 갈 때 키값 넣어주기
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
				
//				holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//					@Override
//					public boolean onLongClick(View v) {
//						if(toggleSelection(se))
//					}
//				});
			}
			
			@Override
			public void onDataChanged() {
				// 데이터가 다 바뀌면 로딩화면 제거
				mProgessStop();
				super.onDataChanged();
			}
		};
		// 어뎁터와 리사이클러뷰 연결
		mRecycler.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 어뎁터가 있으면 실시간 연결
		if (mAdapter != null) {
			mProgessStart();
			mAdapter.startListening();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// 페이지가 멈출 때 실시간 연결기능 해제
		if (mAdapter != null) {
			mAdapter.stopListening();
		}
	}
	
	
	
	// 로딩 모양 보여주는 메소드
	private void mProgessStart() {
		findViewById(R.id.moneyflow_list_recyclerview).setVisibility(View.GONE);
		findViewById(R.id.moneyflow_recycler_progress).setVisibility(View.VISIBLE);
	}
	
	// 로딩이 끝난 경우 로딩 모양 없애주는
	private void mProgessStop() {
		findViewById(R.id.moneyflow_list_recyclerview).setVisibility(View.VISIBLE);
		findViewById(R.id.moneyflow_recycler_progress).setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//네비게이션 버튼 클릭시 이동하는 인텐트
			case R.id.moneyflow_list_bottomBar_dashboardicon:
				startActivity(new Intent(this, MainTestActivity.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				break;
			case R.id.moneyflow_list_bottomBar_messengericon:
				startActivity(new Intent(this, MessengerChatRoomList.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.moneyflow_list_bottomBar_myinfoicon:
				startActivity(new Intent(this, SignInAccountInfo.class));
				// 애니메이션
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			default:
				break;
		}
	}
	
//	public boolean toggleSelection(String key) {
//		boolean b;
//		if (selectedItems.get(key)!=null) {
//			selectedItems.remove(key);
//			b = false;
//		}
//		else {
//			selectedItems.put(key, key);
//			b = true;
//		}
//		int n = getSelectedItemCount();
//		if (n>0 && actionMode!=null) {
//			actionMode.setTitle(String.valueOf(n) + (n==1 ? " selecionado" : " selecionados"));
//		}
//		return b;
//	}
//
//	private void removeItems() {
//		String userId = fragment.getUid();
//
//		Iterator iterator = selectedItems.keySet().iterator();
//		while(iterator.hasNext()) {
//			String key=(String) iterator.next();
//			fragment.mDatabase.child("user-news").child(userId).child(key).removeValue();
//		}
//		selectedItems.clear();
//	}
}
