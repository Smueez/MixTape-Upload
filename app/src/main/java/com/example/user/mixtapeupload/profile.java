package com.example.user.mixtapeupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;

public class profile extends AppCompatActivity {
    ImageView imageView;
    String TAG = "profile view ", urist,email_str;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser muser;
    DatabaseReference mfDatabase,data;
    TextView textView1;
    ProgressBar progressBar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = findViewById(R.id.imageView6);
        auth = FirebaseAuth.getInstance();
        muser = auth.getCurrentUser();
        email_str = muser.getEmail();
        email_str = email_str.replace(".com","");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile").child(email_str);
        mfDatabase = FirebaseDatabase.getInstance().getReference().child("profile").child(email_str).child("imageuri");
        data = FirebaseDatabase.getInstance().getReference().child("profile").child(email_str);
        textView1 = findViewById(R.id.textView15);
        progressBar1 = findViewById(R.id.progressBar2);
        progressBar1.setVisibility(View.VISIBLE);
    }
    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;


        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                urist = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+urist);
                if(urist != null) {
                    storageReference.child(urist).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: got url of image!");
                            new profile.DownLoadImageTask(imageView).execute(uri.toString());
                            Log.d(TAG, "onSuccess: "+uri.toString());
                            progressBar1.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                            Toast.makeText(getApplicationContext(),"Can not load image!",Toast.LENGTH_LONG).show();
                            progressBar1.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    imageView.setImageResource(R.drawable.profile);
                    progressBar1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: something went wrong!");
            }
        };
        mfDatabase.addValueEventListener(eventListener);
        data.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView1.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,home.class);
        startActivity(intent);
    }
}
