package edu.cmu.pocketsphinx.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FinishSetup extends AppCompatActivity {

    String email,fullname,username, gender,status= "student";
    String idnumber,passcode,Cpasscode;
    private EditText idNumber,password,cpassword;
    private Button finishBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_setup);


        finishBtn = findViewById(R.id.register_btnFinish);
        idNumber = findViewById(R.id.regis_idnumber);
        progressBar = findViewById(R.id.mProgressBar);

        password = findViewById(R.id.regis_password);
        cpassword = findViewById(R.id.regis_password_confirm);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();



        Intent i = getIntent();
        email = i.getStringExtra("email");
        fullname = i.getStringExtra("flname");
        username = i.getStringExtra("usname");
        gender = i.getStringExtra("gender");

        onClickForFinishBtn();


    }

    private void onClickForFinishBtn() {
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idnumber = idNumber.getText().toString().trim();
                passcode = password.getText().toString().trim();
                Cpasscode = cpassword.getText().toString().trim();


                /* validation */
                if(!TextUtils.isEmpty(idnumber) && !TextUtils.isEmpty(passcode) && !TextUtils.isEmpty(Cpasscode)){
                    if(!passcode.equals(Cpasscode)){
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        cpassword.getText().clear();
                        cpassword.getText().clear();
                        cpassword.requestFocus();
                    }else {

                        progressBar.setVisibility(View.VISIBLE);

                        /* create user into firebase database */
                        firebaseAuth.createUserWithEmailAndPassword(email, Cpasscode).addOnCompleteListener(FinishSetup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user information = new user(email, username,fullname, idnumber, gender,status);

                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(FinishSetup.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(FinishSetup.this, MainActivity.class));
                                            finish();
                                        }
                                    });


                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(FinishSetup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Please do not leave any field blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
