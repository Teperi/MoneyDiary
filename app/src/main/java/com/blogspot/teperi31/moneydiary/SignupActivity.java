package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
	private FirebaseAuth mAuth;
	private Toolbar mToolbar;
	private EditText mIDField;
	private EditText mNicknameField;
	private EditText mPasswordField;
	private EditText mPasswordAgainField;
	private DatabaseReference mDatabase;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		// 툴바 제작
		mToolbar = findViewById(R.id.signup_toolbarTop);
		setSupportActionBar(mToolbar);
		
		// 뒤로가기 버튼 만들기
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		// 변수 연결
		mIDField = findViewById(R.id.signup_inputid);
		mNicknameField = findViewById(R.id.signup_nickname);
		mPasswordField = findViewById(R.id.signup_password);
		mPasswordAgainField = findViewById(R.id.signup_passwordagain);
		
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();
		
		// 가입하기 버튼을 누를 경우 동작 발생
		findViewById(R.id.signup_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createAccount(mIDField.getText().toString(), mPasswordField.getText().toString(), mNicknameField.getText().toString());
			}
		});
		
		
	}
	
	private void createAccount(String email, String password, final String Nickname) {
		// 형식에 맞지 않는 경우 여기서 끝내기
		if(!validateForm()){
			return;
		}
		
		// 이메일과 패스워드를 보내서 가입시키기
		// 가입 요청을 보낸 후 결과를 받는 리스너 삽입
		// 완료 된 경우 페이지를 이동시키고 아닐경우
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if(task.isSuccessful()) {
					final FirebaseUser user = mAuth.getCurrentUser();
					updateProfile(user, Nickname);
					
					mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							for(DataSnapshot ds : dataSnapshot.getChildren()){
								ds.child("isCurrent").getRef().setValue(false);
							}
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
						
						}
					});
					
					mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							DataUser datauser;
							// 유저 정보를 모아서
							if(user.getPhotoUrl() == null){
								datauser = new DataUser(user.getUid(), user.getDisplayName(), user.getEmail(), null);
							} else {
								datauser = new DataUser(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
							}
							
							Map<String, Object> inputUserData = datauser.toMap();
							Map<String, Object> childUpdates = new HashMap<>();
							// Map 에 한번에 저장 후
							childUpdates.put("/users/" + user.getUid(), inputUserData);
							// 데이터베이스에 집어넣기
							mDatabase.updateChildren(childUpdates);
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
						
						}
					});
					
					
					// TODO : 고민해야 할 부분, 이걸 어떻게 처리하지?
					// update profile 이 에러나면?? 그리고 Nickname 이 없다면??
					Toast.makeText(SignupActivity.this, "가입 완료 되었습니다. \n"+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(SignupActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				} else {
					Log.w("signInWithEmail:failure", task.getException());
					Toast.makeText(SignupActivity.this, "가입에 실패하였습니다. \n 조금 후에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	// 형식에 맞지 않는 경우 error 메시지 출력하기
	private boolean validateForm() {
		boolean valid = true;
		
		// ID 전용
		String ID = mIDField.getText().toString();
		if(TextUtils.isEmpty(ID)) {
			mIDField.setError("아이디를 입력하세요.");
			valid = false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(ID).matches()) {
			mIDField.setError("이메일 주소를 입력하세요.");
			valid = false;
		} else {
			mIDField.setError(null);
		}
		
		// Password 전용
		String password_1 = mPasswordField.getText().toString();
		String password_2 = mPasswordAgainField.getText().toString();
		// 첫번째 칸 확인
		if (TextUtils.isEmpty(password_1)) {
			mPasswordField.setError("비밀번호를 입력하세요.");
			valid = false;
		} else {
			mPasswordField.setError(null);
		}
		
		// 두번째 칸 확인
		if (TextUtils.isEmpty(password_2)) {
			mPasswordAgainField.setError("비밀번호를 입력하세요.");
			valid = false;
		} else if (!password_1.equals(password_2)) {
			mPasswordAgainField.setError("비밀번호가 일치하지 않습니다.");
			valid = false;
		} else {
			mPasswordAgainField.setError(null);
		}
		
		return valid;
	}
	
	/*
	* 아이디 생성 이후 Profile 에 NickName 설정
	* */
	public void updateProfile(FirebaseUser user , String NickName) {
		// [START update_profile]
		UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
				.setDisplayName(NickName)
				.build();
		
		user.updateProfile(profileUpdates)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Log.d("signup", "User profile updated.");
						} else {
							Toast.makeText(SignupActivity.this, "닉네임이 설정되지 않았습니다. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
		// [END update_profile]
	}
	
	//뒤로가기 버튼 기능 넣기
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
}

