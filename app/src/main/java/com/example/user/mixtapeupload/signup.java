package com.example.user.mixtapeupload;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {


    FirebaseAuth mAuth;
    StorageReference mStroage;
    DatabaseReference mData;
    EditText email_text,password_text,name_text;
    String email_str, password_str, name_str,gender_str,uristr;
    public String TAG = "app log: ";
    TextView errors;
    FirebaseUser firebaseUser;
    static int result_loead_image  = 1;
    ImageView profile_pic;
    Uri uri;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //addListenerOnButton();
        mAuth = FirebaseAuth.getInstance();
        mStroage = FirebaseStorage.getInstance().getReference().child("profile");
        mData = FirebaseDatabase.getInstance().getReference().child("profile");

        email_text = findViewById(R.id.email);
        password_text = findViewById(R.id.password);
        name_text = findViewById(R.id.username);
        errors = findViewById(R.id.error_msg);
        profile_pic = findViewById(R.id.profile_img);
        intent = new Intent(this,MainActivity.class);
    }
    public void addListenerOnButton(View view) {


        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    gender_str = "male";

                Log.d(TAG, "addListenerOnButton1: "+gender_str);
                break;
            case R.id.videobttn:
                if (checked)
                    gender_str = "female";

                Log.d(TAG, "addListenerOnButton1: "+gender_str);

                break;
        }

    }
    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }

    private String getFileExtention(Uri imguri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imguri));
    }
    public void uploadimg(String str){
        final StorageReference fileref = mStroage.child(str).child(str+"."+getFileExtention(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                uristr = taskSnapshot.getMetadata().getPath();


                Log.d(TAG, "onSuccess: Image uploader to fire base");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signup.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }
    public void signup_button(View view){
        email_str = email_text.getText().toString().trim();
        password_str = password_text.getText().toString().trim();
        name_str = name_text.getText().toString().trim();
        if(email_str != null && name_str != null){
            if(isValidEmailAddress(email_str)){
                if(password_str != null && password_str.length() >= 6){
                    mAuth.createUserWithEmailAndPassword(email_str,password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            firebaseUser = mAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Check your email to verify",Toast.LENGTH_LONG).show();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("false").build();
                                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: name been set!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: name not been set!");
                                        }
                                    });
                                    String email_cut = email_str.replace(".com","");
                                    uploadimg(email_cut);
                                    uristr = "profile/"+email_cut+"/"+email_cut+"."+getFileExtention(uri);
                                    Log.d(TAG, "onComplete: gender "+gender_str);
                                    PassDataProfile dataProfile = new PassDataProfile(name_str,password_str,uristr,gender_str);
                                    mData.child(email_cut).setValue(dataProfile);
                                    startActivity(intent);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Can not verify email",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    errors.setText("Password is empty or or less than 6 latters!");
                }

            } else {
                errors.setText("Please enter a valid email ID!");
            }
        } else {
            errors.setText("Email is empty!");
        }
    }
    public void back_to_signin(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void upload(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,result_loead_image);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == result_loead_image && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            profile_pic.setImageURI(uri);
            Log.d(TAG, "onActivityResult: Pic is loading!");

        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here
        //Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
