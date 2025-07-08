package com.rfid.hf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class HelpGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_guide);

        // Tombol back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(HelpGuide.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void toggleCard1(View view) {
        View detail = findViewById(R.id.card1_detail);
        detail.setVisibility(detail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void toggleCard2(View view) {
        View detail = findViewById(R.id.card2_detail);
        detail.setVisibility(detail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void toggleCard3(View view) {
        View detail = findViewById(R.id.card3_detail);
        detail.setVisibility(detail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }
}
