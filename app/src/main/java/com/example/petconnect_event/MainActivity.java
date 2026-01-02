package com.example.petconnect_event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect_event.event.activity.EventListActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "MainActivity onCreate started");

        try {
            // Lancer directement la liste des événements
            Log.d(TAG, "Starting EventListActivity");
            startActivity(new Intent(this, EventListActivity.class));
            finish(); // fermer MainActivity
        } catch (Exception e) {
            Log.e(TAG, "Error starting EventListActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}