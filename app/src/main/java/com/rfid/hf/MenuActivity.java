package com.rfid.hf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {
    private ImageButton getStartedNotif;
    private ImageButton getStartedHelpGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getStartedNotif = findViewById(R.id.btn_notif);
        getStartedHelpGuide = findViewById(R.id.btn_help_guid);

        getStartedNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Notif.class);
                startActivity(intent);
                finish();
            }
        });

        getStartedHelpGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HelpGuide.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
