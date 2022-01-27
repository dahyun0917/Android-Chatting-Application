package com.example.chat_de;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConnectedDB {

    private final DatabaseReference connectedRef;
    private static boolean connected = true;
    Context context;
    boolean firstIsTrash = true;

    public ConnectedDB(Context context) {
        this.context = context;
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if(!firstIsTrash && !connected) {
                    Toast.makeText(context,"네트워크 연결이 불안정합니다.",Toast.LENGTH_SHORT).show();
                }
                else
                    firstIsTrash = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.w(TAG, "Listener was cancelled");
            }
        });
    }
}
