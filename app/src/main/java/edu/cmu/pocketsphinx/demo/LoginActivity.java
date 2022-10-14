package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private Button sign_in;
    private TextView sign_up;
    private EditText email;
    private EditText password;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    private FirebaseDatabase user;
    final ArrayList<String> list = new ArrayList<>();








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        sign_in = findViewById(R.id.login_btn);
        sign_up = findViewById(R.id.sign_up);
        email = findViewById(R.id.email);
        password =findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progressBar);


        auth = FirebaseAuth.getInstance();




        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(txt_email,txt_password);
                }
            }
        });
    }








    private void loginUser(final String email, final String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    retrieve_user_info();

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    //function to retrieve user information
    public void retrieve_user_info(){
        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        final String[] status = new String[1];



        databaseReference.child(firebaseUser).addValueEventListener(new ValueEventListener() {
            @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                status[0] = dataSnapshot.child("status").getValue().toString();

                if (!status[0].equals("student")) {
                    Toast.makeText(getApplicationContext(),"Invalid Account",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

    });

    }


}
