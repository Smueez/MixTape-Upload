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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScrean extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Animation animation1,animation2,rotate;
    ImageView imageView;
    FirebaseUser user;
    FirebaseAuth auth;
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
        final Intent intent1 = new Intent(this,home.class);
        imageView.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        new CountDownTimer(3000,2000){
            @Override
            public void onFinish() {
                if(auth.getCurrentUser() == null) {
                    startActivity(intent);
                }
                else {
                    startActivity(intent1);
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                imageView.setAnimation(rotate);
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }
}