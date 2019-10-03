package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Dashboard extends AppCompatActivity {

    private ImageButton restituteBtn, borrowBtn, listBtn;

    Global gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        gv = (Global) getApplicationContext();
        borrowBtn = findViewById(R.id.borrow);
        listBtn = findViewById(R.id.list);
        restituteBtn = findViewById(R.id.restitute);


        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, EquipmentManagement.class);
                startActivity(intent);
            }
        });


    }

}
