package com.rfid.hf;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {
    private ImageButton getStartedNotif;
    private ImageButton getSrartedRFIDScan;
    private ImageButton getStartedStockOpname;
    private ImageButton getStartedGenerateCode;
    private ImageButton getStartedHelpGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getStartedNotif = findViewById(R.id.btn_notif);
        getSrartedRFIDScan = findViewById(R.id.btn_rfid_scan);
        getStartedStockOpname = findViewById(R.id.btn_stock_opname);
        getStartedGenerateCode = findViewById(R.id.btn_generate_code);
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

        getSrartedRFIDScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Connect232.class);
                startActivity(intent);
                finish();
            }
        });

        getStartedStockOpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Stock_Opname.class);
                startActivity(intent);
                finish();
            }
        });

        getStartedGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Generate_Code.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
