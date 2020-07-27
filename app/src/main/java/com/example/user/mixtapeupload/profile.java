package com.example.user.mixtapeupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {
    CircleImageView imageView;
    String TAG = "profile view ", urist,email_str;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser muser;
    DatabaseReference mfDatabase,data;
    TextView textView1;
    ProgressBar progressBar1;
    TextView songsL,videoL;
    EditText editText_change_name;
    LinearLayout linearLayout_edit;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editText_change_name = findViewById(R.id.editText3);
        linearLayout_edit = findViewById(R.id.edit_layout);
        floatingActionButton = findViewById(R.id.floatingActionButton5);
        imageView = findViewById(R.id.profile_image);
        songsL = findViewById(R.id.lovedsong);
        videoL = findViewById(R.id.lovedvid);
        auth = FirebaseAuth.getInstance();
        muser = auth.getCurrentUser();
        email_str = muser.getEmail();
        email_str = email_str.replace(".com","");
        storageReference = FirebaseStorage.getInstance().getReference();
        mfDatabase = FirebaseDatabase.getInstance().getReference().child("profile").child(email_str).child("imageuri");
        data = FirebaseDatabase.getInstance().getReference().child("profile").child(email_str);
        textView1 = findViewById(R.id.textView15);
        progressBar1 = findViewById(R.id.progressBar2);
        progressBar1.setVisibility(View.VISIBLE);
    }
    public static class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
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
                editText_change_name.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    data.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild("audio")){
                int slike1 = (int) dataSnapshot.child("audio").getChildrenCount();
                songsL.setText(String.valueOf(slike1));
            }
            else {
                songsL.setText("0");
            }
            if (dataSnapshot.hasChild("video")){
                int slike2 = (int) dataSnapshot.child("video").getChildrenCount();
                videoL.setText(String.valueOf(slike2));
            }else {
                videoL.setText("0");
            }
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
    public void deact(View view){
        final Intent intent = new Intent(this,MainActivity.class);
        auth.signOut();
        data.removeValue();
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                muser.delete();
                Toast.makeText(getApplicationContext(),"deactivated!",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
    public void change_name(View view){
        String edit_name_text = editText_change_name.getText().toString().trim();
        if (edit_name_text != null){
            data.child("name").setValue(edit_name_text);
            linearLayout_edit.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(getApplicationContext(),"Name can not be empty!",Toast.LENGTH_SHORT).show();
        }
    }
    public void edit(View view){
        linearLayout_edit.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
    }
}
