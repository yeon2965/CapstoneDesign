package com.imaginarywings.capstonedesign.remo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btnMovePeople;
    ImageButton btnMoveLandscape;
    ImageButton btnMovePhotospot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //상태 알림바 없애기
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnMovePeople = (ImageButton) findViewById(R.id.btnPeople);
        btnMoveLandscape = (ImageButton) findViewById(R.id.btnLandscape);
        btnMovePhotospot = (ImageButton) findViewById(R.id.btnPhotospot);

        btnMovePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent peopleIntent = new Intent(getApplicationContext(), PeopleActivity.class);
                startActivity(peopleIntent);
            }
        });

        btnMoveLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent landscapeIntent = new Intent(getApplicationContext(), LandscapeActivity.class);
                startActivity(landscapeIntent);
            }
        });

        btnMovePhotospot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photospotIntent = new Intent(getApplicationContext(), PhotospotActivity.class);
                startActivity(photospotIntent);
            }
        });

    }
}
