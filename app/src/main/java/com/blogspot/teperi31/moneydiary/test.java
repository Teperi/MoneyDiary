package com.blogspot.teperi31.moneydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class test extends AppCompatActivity {
	
	// [START define_database_reference]
	private DatabaseReference mDatabase;
	// [END define_database_reference]
	private RecyclerView mRecycler;
	private LinearLayoutManager mManager;
	private FirebaseRecyclerAdapter<testPost, testViewHolder> mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		
		Toolbar mTB = findViewById(R.id.test_toolbarTop);
		setSupportActionBar(mTB);
		
		
		mDatabase = FirebaseDatabase.getInstance().getReference();
		mRecycler = findViewById(R.id.test_recyclerview);
		
		mRecycler.setHasFixedSize(true);
		
		mManager = new LinearLayoutManager(this);
		mManager.setReverseLayout(true);
		mManager.setStackFromEnd(true);
		mRecycler.setLayoutManager(mManager);
		
		
		Query testpostQuery = mDatabase.child("posts").limitToFirst(100);
		
		FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<testPost>()
				.setQuery(testpostQuery, testPost.class)
				.build();
		
		
		mAdapter = new FirebaseRecyclerAdapter<testPost, testViewHolder>(options){
			
			@Override
			public testViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test_row, viewGroup, false);
				return new testViewHolder(itemView);
			}
			
			@Override
			protected void onBindViewHolder(testViewHolder holder, int position, final testPost model) {
				
				holder.bindToPost(model);
				
				DatabaseReference postref = getRef(position);
				final String postKey = postref.getKey();
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), testDetail.class);
						intent.putExtra(testDetail.EXTRA_POST_KEY, postKey);
						startActivity(intent);
					}
				});
				
				
				
			}
		};
		mRecycler.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mAdapter != null){
			mAdapter.stopListening();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.topbar_input, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.topbar_input_setting:
				Intent i = new Intent(test.this, testInput.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(mAdapter != null){
			mAdapter.startListening();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
}
