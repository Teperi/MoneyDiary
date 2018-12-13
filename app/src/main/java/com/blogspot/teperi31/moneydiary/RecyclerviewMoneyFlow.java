package com.blogspot.teperi31.moneydiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class RecyclerviewMoneyFlow extends AppCompatActivity implements android.support.v7.view.ActionMode.Callback {
	
	// 전화 걸기 권한 허용 요청
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
	
	Toolbar myToolbar;
	
	private RecyclerView mRecyclerView;
	private RecyclerView.LayoutManager mLayoutManager;
	AdapterMFRecycler myAdapter;
	
	// 선택 모드 확인
	private boolean isMultiSelect = false;
	
	// 액션모드 선택을 위한 저장 데이터
	private ArrayList<Integer> selectedIds = new ArrayList<>();
	
	ActionMode actionMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneyflow_list);
		
		
		// 액션 바 삽입
		myToolbar = findViewById(R.id.moneyflow_list_toolbarTop);
		setSupportActionBar(myToolbar);
		
		mRecyclerView = findViewById(R.id.moneyflow_list_recyclerview);
		
		// 사이즈 고정, 리사이클러 뷰에서 content 사이즈를 바꾸지 말라?
		mRecyclerView.setHasFixedSize(true);
		
		// 리니어 레이아웃 매니저. 한줄씩 쌓임
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		
		//내 어댑터와 데이터 연결
		myAdapter = new AdapterMFRecycler(this, ApplicationClass.mfList);
		
		mRecyclerView.setAdapter(myAdapter);
		
		
		// 가격 전체 데이터 저장
		int moneyflowDepositint = 0;
		int moneyflowWithdrawint = 0;
		int moneyflowTotalint;
		TextView moneyflowDeposittext = findViewById(R.id.moneyflowcalcdeposit);
		TextView moneyflowWithdrawtext = findViewById(R.id.moneyflowcalcwithdraw);
		TextView moneyflowTotaltext = findViewById(R.id.moneyflowcalctotal);
		
		// 수입 및 지출 계산
		for (int i = 0; i < ApplicationClass.mfList.size(); i++) {
			if (ApplicationClass.mfList.get(i).MFListType.equals("수입")) {
				moneyflowDepositint += ApplicationClass.mfList.get(i).MFListPrice;
			} else if (ApplicationClass.mfList.get(i).MFListType.equals("지출")) {
				moneyflowWithdrawint += ApplicationClass.mfList.get(i).MFListPrice;
			}
		}
		
		// 총액 계산
		moneyflowTotalint = moneyflowDepositint - moneyflowWithdrawint;
		
		// 텍스트 출력
		moneyflowDeposittext.setText(String.valueOf(moneyflowDepositint));
		moneyflowWithdrawtext.setText(String.valueOf(moneyflowWithdrawint));
		moneyflowTotaltext.setText(String.valueOf(moneyflowTotalint));
		
		moneyflowDeposittext.setTextColor(getColor(R.color.colorPrimaryDark));
		moneyflowWithdrawtext.setTextColor(getColor(R.color.colorError));
		
		
		// RecyclerItemClickListener 를 그
		mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
			
			
			@Override
			public void onItemClick(View view, int position) {
				if (isMultiSelect) {
					//if multiple selection is enabled then select item on single click else perform normal click on item.
					multiSelect(position);
				} else {
					Intent i = new Intent(RecyclerviewMoneyFlow.this, EditMoneyFlowData.class);
					i.putExtra("mfposition", position);
					startActivity(i);
				}
			}
			
			@Override
			public void onItemLongClick(View view, int position) {
				if (!isMultiSelect) {
					selectedIds = new ArrayList<>();
					isMultiSelect = true;
					
					if (actionMode == null) {
						actionMode = startSupportActionMode(RecyclerviewMoneyFlow.this); //show ActionMode.
					}
				}
				
				multiSelect(position);
			}
		}
		
		));
		
		
	}
	
	
	private void multiSelect(int position) {
		DataMoneyFlow data = myAdapter.getItem(position);
		if (data != null) {
			if (actionMode != null) {
				if (selectedIds.contains(data.MFListId))
					selectedIds.remove(Integer.valueOf(data.MFListId));
				else
					selectedIds.add(data.MFListId);
				
				if (selectedIds.size() > 0)
					actionMode.setTitle(String.valueOf(selectedIds.size())); //show selected item count on action mode.
				else {
					actionMode.setTitle(""); //remove item count from action mode.
					actionMode.finish(); //hide action mode.
				}
				myAdapter.setSelectedIDs(selectedIds);
				
			}
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
				Intent i = new Intent(RecyclerviewMoneyFlow.this, InputMoneyFlowCreate.class);
				startActivity(i);
				Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.main_menu_AppInfo:
				Toast.makeText(this, "개발자 블로그로 연결합니다.", Toast.LENGTH_SHORT).show();
				Intent actionBlogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://naver.com"));
				startActivity(actionBlogIntent);
				return true;
			
			case R.id.actionmyemail:
				Toast.makeText(this, "개발자에게 메일을 보냅니다.", Toast.LENGTH_SHORT).show();
				Intent actionEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:teperi31@gmail.com"));
				startActivity(actionEmailIntent);
				return true;
			
			case R.id.main_menu_AppSetting:
				Intent actionTelIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-2061-3823"));
				int permissionCheck = ContextCompat.checkSelfPermission(RecyclerviewMoneyFlow.this, Manifest.permission.CALL_PHONE);
				
				if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					
					ActivityCompat.requestPermissions(RecyclerviewMoneyFlow.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
					
				}
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent actionTelIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-2061-3823"));
					Toast.makeText(this, "개발자에게 전화를 합니다.", Toast.LENGTH_SHORT).show();
					startActivity(actionTelIntent);
				} else {
					Toast.makeText(this, "이 기능은 통화 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
					
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri = Uri.fromParts("package", getPackageName(), null);
					intent.setData(uri);
					startActivity(intent);
				}
				return;
			}
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	
	@Override
	public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		MenuInflater inflater = actionMode.getMenuInflater();
		inflater.inflate(R.menu.actionmode_setting, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		return false;
	}
	
	@Override
	public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()){
			case R.id.actionmodeDelete:
				//just to show selected items.
				ArrayList<Integer> idSelect = new ArrayList<>();
//				StringBuilder stringBuilder2 = new StringBuilder();
				for (DataMoneyFlow data : ApplicationClass.mfList) {
					if (selectedIds.contains(data.MFListId))
						idSelect.add(data.MFListId);
				}
//				Toast.makeText(this, "Selected items are :" + idSelect.toString(), Toast.LENGTH_SHORT).show();
				for(int i : idSelect){
					ApplicationClass.mfList.remove(i);
				}
				UtilPreference.setMoneyflow(this);
				Intent intent = new Intent(RecyclerviewMoneyFlow.this, RecyclerviewMoneyFlow.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				
				return true;
			
		}
		return false;
	}
	
	@Override
	public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
		actionMode = null;
		isMultiSelect = false;
		selectedIds = new ArrayList<>();
		myAdapter.setSelectedIDs(new ArrayList<Integer>());
	}
}
	
	

