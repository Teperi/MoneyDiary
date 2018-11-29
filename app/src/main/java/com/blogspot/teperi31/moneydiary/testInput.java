package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class testInput extends AppCompatActivity {
	
	private DatabaseReference mDatabase;
	// [END declare_database_ref]
	
	private EditText mTitleField;
	private EditText mBodyField;
	FirebaseAuth mAuth;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		mAuth = FirebaseAuth.getInstance();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_input);
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		mTitleField = findViewById(R.id.testinputtitle);
		mBodyField = findViewById(R.id.testinputcontent);
		
		findViewById(R.id.testinputsubmit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String title = mTitleField.getText().toString();
				final String body = mBodyField.getText().toString();
				
				// Title is required
				if (TextUtils.isEmpty(title)) {
					mTitleField.setError("REQUIRED");
					return;
				}
				
				// Body is required
				if (TextUtils.isEmpty(body)) {
					mBodyField.setError("REQUIRED");
					return;
				}
				
				Toast.makeText(testInput.this, "Posting...", Toast.LENGTH_SHORT).show();
				
				final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
				
				mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
						new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								User user = dataSnapshot.getValue(User.class);
								
								if(user == null){
									Toast.makeText(testInput.this,
											"Error: could not fetch user.",
											Toast.LENGTH_SHORT).show();
								} else {
									writeNewPost(userId, user.username, title, body);
								}
								
								finish();
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
							
							}
						}
				);
				
			}
		});
		
	}
	
	private void writeNewPost(String userId, String username, String title, String body) {
		// Create new post at /user-posts/$userid/$postid and at
		// /posts/$postid simultaneously
		// key 는 새로운 키 생성
		String key = mDatabase.child("posts").push().getKey();
		// 데이터베이스에 데이터 목록을 추가
		testPost post = new testPost(userId, username, title, body);
		// 포스트 내용을 Map으로 바꿈
		// userId : 11
		// username :11
		// 이런 형식으로 저장됨
		// JSON 에 전달할 수 있는 유형의 Map 만들기
		Map<String, Object> postValues = post.toMap();
		
		Map<String, Object> childUpdates = new HashMap<>();
		// post 와 user-post 에 둘 다 넣기 위해 HashMap 생성해서 집어넣기
		// /posts/ + key 해놓으면 그게 경로가 되는 구조 (posts 안에 key 값 안에 집어넣어)
		childUpdates.put("/posts/" + key, postValues);
		childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
		
		
		// HashMap 데이터베이스에 집어넣기
		mDatabase.updateChildren(childUpdates);
	}
	
	private void writeNewUser(String userId, String name, String email) {
		User user = new User(name, email);
		
		mDatabase.child("users").child(userId).setValue(user);
	}
}
