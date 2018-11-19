package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

public class DairyCreateInput extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcreatedairy);
		
		// 툴바 입력
		Toolbar myToolbar = findViewById(R.id.my_toolbarTop_basic);
		setActionBar(myToolbar);
		
		
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		
		
		EditText sharecontent = findViewById(R.id.inputCreateDairyContent);
		ImageView shareimage = findViewById(R.id.inputCreateDairyImage);
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (sharedText != null) {
					sharecontent.setText(sharedText);
				} else if (type.startsWith("image/")) {
					Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
					if (imageUri != null) {
						shareimage.setImageURI(imageUri);
					}
				}
			}
		}
		
		
		
		
		
		
		
	}
	
	
}
