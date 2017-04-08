package com.imaginarywings.capstonedesign.remo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by S.JJ on 2017-04-05.
 */

public class PeopleActivity extends Activity
{
    Button btn = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        btn = (Button) findViewById(R.id.btnCamera);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);

            }
        });
    }
}
