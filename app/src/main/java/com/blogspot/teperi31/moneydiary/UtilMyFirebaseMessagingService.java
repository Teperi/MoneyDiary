package com.blogspot.teperi31.moneydiary;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

public class UtilMyFirebaseMessagingService extends FirebaseMessagingService {
	@Override
	public void onNewToken(String token) {
		super.onNewToken(token);
		
		sendRegistrationToServer(token);
	}
	
	private void sendRegistrationToServer(final String token) {
		FirebaseDatabase.getInstance().getReference().child("FCMID").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.getChildrenCount() <= 0) {
					dataSnapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
				} else if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
					for (DataSnapshot item:dataSnapshot.getChildren()){
						if(item.getValue().equals(token)){
							item.getRef().removeValue();
						}
					}
					dataSnapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
				} else if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
						!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().equals(token)) {
					for (DataSnapshot item:dataSnapshot.getChildren()){
						if(item.getValue().equals(token)){
							item.getRef().removeValue();
						}
					}
					dataSnapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
	}
	
	
	
	
}
