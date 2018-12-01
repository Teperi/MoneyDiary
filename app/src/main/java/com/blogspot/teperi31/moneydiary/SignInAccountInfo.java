package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInAccountInfo extends AppCompatActivity implements View.OnClickListener{
	
	private FirebaseAuth mAuth;
	private EditText mIDField;
	private EditText mPasswordField;
	private TextView mIDText;
	private ProgressBar mProgressView;
	private ScrollView mLoginView;
	private ScrollView mLogoutView;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.signin_toobarTop);
		setSupportActionBar(mToolbar);
		
		//
		mIDField = findViewById(R.id.signin_inputid);
		mPasswordField = findViewById(R.id.signin_inputpassword);
		mIDText = findViewById(R.id.signin_idresult);
		mProgressView = findViewById(R.id.signin_progress);
		mLoginView = findViewById(R.id.signin_loginscrollview);
		mLogoutView = findViewById(R.id.signin_logoutscrollview);
		
		
		mAuth = FirebaseAuth.getInstance();
		
		findViewById(R.id.signin_loginButton).setOnClickListener(this);
		findViewById(R.id.signin_logoutButton).setOnClickListener(this);
		findViewById(R.id.signin_signupButton).setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mProgressView.setVisibility(View.VISIBLE);
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			updateUI(currentUser);
		} else {
			mProgressView.setVisibility(View.GONE);
		}
	}
	
	// 현재 로그인 토큰이 있는 경우 바로 다음 Activity로 넘어가기
	private void onAuthSuccess() {
		
		mProgressView.setVisibility(View.GONE);
		// Go to MainActivity : 메인으로 이동
		startActivity(new Intent(SignInAccountInfo.this, MainActivity.class));
		// 이 페이지는 종료시킴
		finish();
	}
	
	
	
	
	private void signIn(String ID, String password) {
		if (!validateForm()) {
			return;
		}
		mProgressView.setVisibility(View.VISIBLE);
		
		mAuth.signInWithEmailAndPassword(ID, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if(task.isSuccessful()) {
					FirebaseUser user = mAuth.getCurrentUser();
					Toast.makeText(SignInAccountInfo.this, "로그인 하셨습니다.\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
					onAuthSuccess();
					finish();
				} else {
					// TODO : 로그인에 실패했을 경우 어떤 것이 틀렸는지 받아올 수 있는지 확인하기
					Toast.makeText(SignInAccountInfo.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
					updateUI(null);
				}
				mProgressView.setVisibility(View.GONE);
				
			}
		});
		
	}
	
	private void signOut() {
		mAuth.signOut();
		updateUI(null);
	}
	
	private boolean validateForm() {
		boolean valid = true;
		
		String email = mIDField.getText().toString();
		// ID 전용
		String ID = mIDField.getText().toString();
		if (TextUtils.isEmpty(ID)) {
			mIDField.setError("아이디를 입력하세요.");
			valid = false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(ID).matches()) {
			mIDField.setError("이메일 주소를 입력하세요.");
			valid = false;
		} else {
			mIDField.setError(null);
		}
		//Password error 전용
		String password = mPasswordField.getText().toString();
		
		if (TextUtils.isEmpty(password)) {
			mPasswordField.setError("비밀번호를 입력하세요.");
			valid = false;
		} else {
			mPasswordField.setError(null);
		}
		return valid;
	}
	
	
	private void updateUI(FirebaseUser user) {
		mProgressView.setVisibility(View.GONE);
		// 로그인 되어있는 경우
		if (user != null) {
			// Logout 버튼이 있는 창 띄우기
			mLoginView.setVisibility(View.GONE);
			mLogoutView.setVisibility(View.VISIBLE);
			// ID 를 보일 수 있는 부분에 ID 넣기
			mIDText.setText(user.getEmail());
		}
		// 로그인 안되있을 경우
		else {
			// 로그인 버튼이 있는 창 띄우기
			mLogoutView.setVisibility(View.GONE);
			mLoginView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.signin_loginButton:
				signIn(mIDField.getText().toString(), mPasswordField.getText().toString());
				return;
			case R.id.signin_logoutButton:
				signOut();
				return;
			case R.id.signin_signupButton:
				Intent intent = new Intent(this, SignupActivity.class);
				startActivity(intent);
				return;
			default:
				return;
		}
	}
	
	//뒤로가기 버튼 기능 넣기
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	
	
}
