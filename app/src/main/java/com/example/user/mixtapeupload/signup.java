package com.example.user.mixtapeupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class signup extends AppCompatActivity {

    RadioGroup radioSexGroup;
    RadioButton radioButton;
    public String TAG = "app log: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        radioSexGroup = findViewById(R.id.gender);
        addListenerOnButton();
    }
    public void addListenerOnButton() {

        radioSexGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioSexGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton =  findViewById(selectedId);

                Log.d(TAG, "onClick: "+radioButton.getText().toString().trim());

            }

        });

    }

}
