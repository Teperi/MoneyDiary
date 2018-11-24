package com.blogspot.teperi31.moneydiary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
	// 리스너를 사용할때 기본적으로 필요한 부분 체크
	// 아이템 클릭과 long클릭만 필요하므로 두개만 override 하게 설정
	public interface OnItemClickListener {
		void onItemClick(View view, int position);
		
		void onItemLongClick(View view, int position);
	}
	
	// 아이템 클릭 및 제스쳐추적
	private OnItemClickListener mListener;
	private GestureDetector mGestureDetector;
	
	// 리사이클러 아이템 클릭시 받아오는 리스너 생성
	public RecyclerItemClickListener(final Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
		mListener = listener;
		
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			
			// 누른 후 떼어야 한번 인식되기 위한 코드
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
				// 오버라이드 하면 있는 코드 : return super.onSingleTapUp(e);
			}
			
			// 길게 누르고 있다면
			@Override
			public void onLongPress(MotionEvent e) {
				// 길게 누른 자식 뷰 가져오기
				View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
				// 선택된 자식뷰 있고 리스너 있을 경우
				if (childView != null && mListener != null) {
					// 리스너의 ItemLongClick 으로 넘기기
					mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
				}
				// 오버라이드 하면 있는 코드 : super.onLongPress(e);
			}
		});
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
		// 선택된 자식 View 가져오기
		View childView = view.findChildViewUnder(e.getX(), e.getY());
		// 선택된 자식뷰 있고 리스너 있을 경우 터치 감지된 경우
		if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
			// onItemClick 으로 넘기기
			mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
		}
		// 전체 View그룹에서의 클릭을 하위 View 로 가져가기 위한 코드
		// 상위 뷰에서 클릭하던 것을 가로채서 넘겨주어야 한다. 그걸 위해 false 선언 필요함
		return false;
	}
	
	@Override
	public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
	
	}
	
	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean b) {
	
	}
}
