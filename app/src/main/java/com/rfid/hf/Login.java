package com.rfid.hf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private final String CORRECT_PIN = "24034";
    private ArrayList<Integer> enteredPin = new ArrayList<>();
    private TextView[] pinBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi pin box
        pinBoxes = new TextView[]{
                findViewById(R.id.pin_box_1),
                findViewById(R.id.pin_box_2),
                findViewById(R.id.pin_box_3),
                findViewById(R.id.pin_box_4),
                findViewById(R.id.pin_box_5),
        };

        // Inisialisasi semua tombol angka
        initNumberButton(R.id.btn_0, 0);
        initNumberButton(R.id.btn_1, 1);
        initNumberButton(R.id.btn_2, 2);
        initNumberButton(R.id.btn_3, 3);
        initNumberButton(R.id.btn_4, 4);
        initNumberButton(R.id.btn_5, 5);
        initNumberButton(R.id.btn_6, 6);
        initNumberButton(R.id.btn_7, 7);
        initNumberButton(R.id.btn_8, 8);
        initNumberButton(R.id.btn_9, 9);

        // Tombol back: kembali ke WelcomeActivity
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Welcome.class);
            startActivity(intent);
            finish();
        });
    }

    private void initNumberButton(int buttonId, int number) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (enteredPin.size() < 5) {
                enteredPin.add(number);
                updatePinBoxes();

                if (enteredPin.size() == 5) {
                    checkPin();
                }
            }
        });
    }

    private void updatePinBoxes() {
        for (int i = 0; i < pinBoxes.length; i++) {
            if (i < enteredPin.size()) {
                pinBoxes[i].setText("•");
            } else {
                pinBoxes[i].setText("");
            }
        }
    }

    private void checkPin() {
        StringBuilder pinBuilder = new StringBuilder();
        for (int digit : enteredPin) {
            pinBuilder.append(digit);
        }

        if (pinBuilder.toString().equals(CORRECT_PIN)) {
            // PIN benar → pindah ke MenuActivity
            Intent intent = new Intent(Login.this, MenuActivity.class);
            startActivity(intent);
            finish();
        } else {
            // PIN salah → reset
            Toast.makeText(this, "PIN salah, coba lagi", Toast.LENGTH_SHORT).show();
            enteredPin.clear();
            updatePinBoxes();
        }
    }
}