package com.example.user.mixtapeupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SplashScrean extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Animation animation1,animation2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screan);

        linearLayout = findViewById(R.id.imagelayout);
        relativeLayout = findViewById(R.id.textlayout);
        animation1 = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        linearLayout.setAnimation(animation1);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        relativeLayout.setAnimation(animation2);

    }
}