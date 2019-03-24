package com.example.user.mixtapeupload;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText email_edit, password_edit;
    TextView textView;
    String email_str, password_str;
    FirebaseAuth mAuth;
    public String TAG = "app log: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();
        email_edit = findViewById(R.id.email_login);
        password_edit = findViewById(R.id.pass_login);
        textView = findViewById(R.id.textView2);
    }
    public void signin(View view){
        email_str = email_edit.getText().toString().trim();
        password_str = password_edit.getText().toString().trim();
        if(email_str != null && password_str != null) {
            mAuth.signInWithEmailAndPassword(email_str, password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Email or Password is wrong");
                }
            });
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Email or Password is empty!");
        }
    }
    public void exit(View view){

    }
    public void go_to_signup(View view){
        Intent intent = new Intent(this,signup.class);
        startActivity(intent);
    }
}
