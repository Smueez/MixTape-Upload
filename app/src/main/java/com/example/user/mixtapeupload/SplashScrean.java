package com.example.user.mixtapeupload;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SplashScrean extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Animation animation1,animation2,rotate;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screan);

        imageView = findViewById(R.id.imageView2);
        linearLayout = findViewById(R.id.imagelayout);
        relativeLayout = findViewById(R.id.textlayout);
        animation1 = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        linearLayout.setAnimation(animation1);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        relativeLayout.setAnimation(animation2);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotation);
        final Intent intent = new Intent(this,MainActivity.class);
        imageView.setVisibility(View.VISIBLE);
        new CountDownTimer(3000,2000){
            @Override
            public void onFinish() {
                startActivity(intent);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                imageView.setAnimation(rotate);
            }
        }.start();
    }
}