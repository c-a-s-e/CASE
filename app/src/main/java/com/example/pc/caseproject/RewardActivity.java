package com.example.pc.caseproject;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class RewardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        ImageView reward_image = (ImageView) findViewById(R.id.reward_money);
        int flag = -1;
        flag = getIntent().getIntExtra("user_flag", 1);
        if(flag == 0) {
            Glide.with(this).load(R.drawable.reward_).into(reward_image);
        }
        else if(flag == 1) {
            Glide.with(this).load(R.drawable.reward_suppoter).into(reward_image);
        }
        else {
            Glide.with(this).load(R.drawable.mylocation).into(reward_image);
        }
    }
}
