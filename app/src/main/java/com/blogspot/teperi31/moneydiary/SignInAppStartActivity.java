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
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
* 처음 시작할 때 Splash 이후 들어오는 창
* 로그인 여부 확인
* 로그인 토큰이 있는 경우 이 페이지를 바로 넘어가면서 Toast 로 자동로그인 상태를 알림
* 아닐 경우 로그인 유도 및 가입 유도
* */
public class SignInAppStartActivity extends AppCompatActivity implements View.OnClickListener {
	
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
		
		mIDField = findViewById(R.id.signin_inputid);
		mPasswordField = findViewById(R.id.signin_inputpassword);
		mProgressView = findViewById(R.id.signin_progress);
		mLoginView = findViewById(R.id.signin_loginscrollview);
		
		
		mAuth = FirebaseAuth.getInstance();
		
		findViewById(R.id.signin_loginButton).setOnClickListener(this);
		findViewById(R.id.signin_logoutButton).setOnClickListener(this);
		findViewById(R.id.signin_signupButton).setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 로딩 띄우기
		mProgressView.setVisibility(View.VISIBLE);
		
		// 현재 유저 상태 가져와서
		FirebaseUser currentUser = mAuth.getCurrentUser();
		// 있으면 넘기고 아니면 Signin 창 띄우기
		if (currentUser != null) {
			onAuthSuccess();
		} else {
			mProgressView.setVisibility(View.GONE);
		}
	}
	
	// 현재 로그인 토큰이 있는 경우 바로 다음 Activity로 넘어가기
	private void onAuthSuccess() {
		
		mProgressView.setVisibility(View.GONE);
		// Go to MainActivity : 메인으로 이동
		Toast.makeText(this, "자동 로그인 되었습니다.\n" + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
		startActivity(new Intent(SignInAppStartActivity.this, MainActivity.class));
		// 이 페이지는 종료시킴
		finish();
	}
	
	
	
	// 로그인 버튼 눌렀을 시 행동
	private void signIn(String ID, String password) {
		if (!validateForm()) {
			return;
		}
		mProgressView.setVisibility(View.VISIBLE);
		// 서버안에 키값이 있는지 확인하고 결과값 받아옴
		mAuth.signInWithEmailAndPassword(ID, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if(task.isSuccessful()) {
					FirebaseUser user = mAuth.getCurrentUser();
					Toast.makeText(SignInAppStartActivity.this, "로그인 하셨습니다.\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
					onAuthSuccess();
					
				} else {
					// TODO : 로그인에 실패했을 경우 어떤 것이 틀렸는지 받아올 수 있는지 확인하기
					Toast.makeText(SignInAppStartActivity.this, "로그인에 실패했습니다. \n", Toast.LENGTH_SHORT).show();
				}
				mProgressView.setVisibility(View.GONE);
				
			}
		});
		
	}
	
	// 로그인 폼에 에러 있는지 확인
	private boolean validateForm() {
		boolean valid = true;
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
	
	// 클릭했을 때의 함수 이동
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.signin_loginButton:
				signIn(mIDField.getText().toString(), mPasswordField.getText().toString());
				return;
			case R.id.signin_signupButton:
				Intent intent = new Intent(this, SignupActivity.class);
				startActivity(intent);
				return;
			default:
				return;
		}
	}
	
	
}
