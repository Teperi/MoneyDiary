package com.blogspot.teperi31.moneydiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

public class SpendCreateInput extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatespend);
		
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop);
		setActionBar(myToolbar);
	}
}