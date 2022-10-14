package edu.cmu.pocketsphinx.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SuspectDetails extends AppCompatActivity {
    private String susKey = null;
    private DatabaseReference mDatabase;
    private TextView susNameDetail, susStatusDetail, susProgrammeDetail, susDetailDetail;
    private ImageView susImageDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspect_details);

        String sus_key = getIntent().getExtras().getString("sus_Id");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Suspects");

        susNameDetail = findViewById(R.id.nameOfSuspectDetail);
        susProgrammeDetail = findViewById(R.id.programOfSuspectDetail);
        susStatusDetail = findViewById(R.id.statusOfSuspectDetail);
        susDetailDetail = findViewById(R.id.susDetailsDetail);
        susImageDetail = findViewById(R.id.susDetailImg);

        assert sus_key != null;
        mDatabase.child(sus_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String susName = dataSnapshot.child("Suspect_Name").getValue().toString();
                String susDetails = dataSnapshot.child("Suspect_Details").getValue().toString();
                String susProgramme = dataSnapshot.child("Suspect_Programme").getValue().toString();
                String susStatus = dataSnapshot.child("Suspect_Status").getValue().toString();
                String susImage = dataSnapshot.child("image").getValue().toString();
                susNameDetail.setText(susName);
                susProgrammeDetail.setText(susProgramme);
                susStatusDetail.setText(susStatus);
                susDetailDetail.setText(susDetails);

                Picasso.get().load(susImage).into(susImageDetail);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
