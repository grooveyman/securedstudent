
package edu.cmu.pocketsphinx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NextRegister extends AppCompatActivity {

    private Spinner genderSpinner;
    private Button nextBtn;
    private EditText username,fulname;
    private String genderString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_register);

        /* getting user email from previous activity */
        Intent i = getIntent();
        final String email = i.getStringExtra("fromEmail");


        nextBtn = findViewById(R.id.nextBtn2);
        username = findViewById(R.id.regis_username);
        fulname = findViewById(R.id.regis_fullname);

        addItemSpinner();


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = username.getText().toString().trim();
                String fname = fulname.getText().toString().trim();

                /* validation */
                if(!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(fname)){
                    Intent i = new Intent(getApplicationContext(), FinishSetup.class);
                    i.putExtra("email", email);
                    i.putExtra("usname", uname);
                    i.putExtra("flname", fname);
                    i.putExtra("gender", genderString);

                    startActivity(i);
                }else {
                    Toast.makeText(NextRegister.this,"Please enter a valid username and or your full name", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void addItemSpinner() {
        genderSpinner = findViewById(R.id.genderSpinner);
        List<String> gender = new ArrayList<String>();
        gender.add("Male");
        gender.add("Female");

        ArrayAdapter<String> dataAdpater =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, gender);
        dataAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdpater);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderString = genderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
