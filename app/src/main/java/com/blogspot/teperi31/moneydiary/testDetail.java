package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class testDetail extends AppCompatActivity {
	private String mPostKey;
	private TextView mTitleView;
	private TextView mBodyView;
	private DatabaseReference mPostReference;
	private ValueEventListener mPostListener;
	public static final String EXTRA_POST_KEY = "post_key";
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_detail);
		
		mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
		if(mPostKey == null) {
			throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
		}
		
		mPostReference = FirebaseDatabase.getInstance().getReference()
				.child("posts").child(mPostKey);
		
		mTitleView = findViewById(R.id.test_detail_title);
		mBodyView = findViewById(R.id.test_detail_content);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		ValueEventListener postLinstener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				testPost post = dataSnapshot.getValue(testPost.class);
				
				mTitleView.setText(post.title);
				mBodyView.setText(post.content);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(testDetail.this, "로딩 오류", Toast.LENGTH_SHORT).show();
				
			}
		};
		
		mPostReference.addValueEventListener(postLinstener);
		
	}
}
