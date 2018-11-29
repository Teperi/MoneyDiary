package com.blogspot.teperi31.moneydiary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class testViewHolder extends RecyclerView.ViewHolder {
	public TextView testTitle;
	public TextView testContent;
	
	public testViewHolder(View v) {
		super(v);
		testTitle = v.findViewById(R.id.testTitle);
		testContent = v.findViewById(R.id.testContent);
	}
	
	public void bindToPost(testPost post) {
		testTitle.setText(post.title);
		testContent.setText(post.content);
	}
	
	
}
