package com.blogspot.teperi31.moneydiary;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop);
		setSupportActionBar(myToolbar);
		
		
		CalendarView calendarView = findViewById(R.id.calender1);
		calendarView.setDate(System.currentTimeMillis());
		
		
		
		
		findViewById(R.id.spendingInput).setOnClickListener(
				new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						Intent i = new Intent(MainActivity.this, SpendCreateInput.class);
						startActivity(i);
					}
				}
		);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_actions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionAdd:
				Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
				return true;
			
			case R.id.actionSearch:
				Toast.makeText(this, "검색", Toast.LENGTH_SHORT).show();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
}
