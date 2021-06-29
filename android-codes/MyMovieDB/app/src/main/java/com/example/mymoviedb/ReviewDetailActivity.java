package com.example.mymoviedb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ReviewDetailActivity extends AppCompatActivity {

    private TextView textView_voting;
    private TextView textView_nameAndTime;
    private TextView textView_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        Intent intent = getIntent();
        Double voting = intent.getDoubleExtra("voting", 0);
        String name = intent.getStringExtra("name");
        String creationTime = intent.getStringExtra("creationTime");
        String content = intent.getStringExtra("content");

        textView_voting = findViewById(R.id.review_detail_voting);
        textView_voting.setText(voting + "/5");

        textView_nameAndTime = findViewById(R.id.review_detail_nameAndTime);
        textView_nameAndTime.setText("by " + name + " on " + creationTime);

        textView_content = findViewById(R.id.review_detail_content);
        textView_content.setText(content);


    }
}