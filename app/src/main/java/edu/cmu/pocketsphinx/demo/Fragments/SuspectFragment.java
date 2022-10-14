package edu.cmu.pocketsphinx.demo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yalantis.phoenix.PullToRefreshView;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.cmu.pocketsphinx.demo.R;
import edu.cmu.pocketsphinx.demo.Suspect;
import edu.cmu.pocketsphinx.demo.SuspectDetails;

public class SuspectFragment extends Fragment {

    private View SuspectsView;
    private RecyclerView mySuspectList;

    private DatabaseReference suspectReference;

    private PullToRefreshView mPullTorefreshView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SuspectsView = inflater.inflate(R.layout.fragment_suspect, container, false);
        mySuspectList = SuspectsView.findViewById(R.id.viewSuspects);
        mySuspectList.setLayoutManager(new LinearLayoutManager(getContext()));

        suspectReference = FirebaseDatabase.getInstance().getReference().child("Suspects");
        mPullTorefreshView = SuspectsView.findViewById(R.id.pull_to_refresh);

        mPullTorefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            private static final long REFRESH_DELAY = 2000;

            @Override
            public void onRefresh() {

                FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Suspect>()
                        .setQuery(suspectReference, Suspect.class)
                        .build();

                FirebaseRecyclerAdapter<Suspect, SuspectViewHolder> adapter = new FirebaseRecyclerAdapter<Suspect, SuspectViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SuspectViewHolder suspectViewHolder, int i, @NonNull Suspect suspect) {
                        final String suspectIds = getRef(i).getKey();

                        suspectReference.child(suspectIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("image")){
                                    String suspectImage = dataSnapshot.child("image").getValue().toString();
                                    String profileName = dataSnapshot.child("Suspect_Name").getValue().toString();
                                    String profileProgramme = dataSnapshot.child("Suspect_Programme").getValue().toString();
                                    String profileStatus = dataSnapshot.child("Suspect_Status").getValue().toString();
//                            if(dataSnapshot.child("Sus_Status").getValue().toString() == null){
//
//                            }

                                    suspectViewHolder.suspectName.setText(profileName);
                                    suspectViewHolder.suspectProgramme.setText(profileProgramme);
                                    suspectViewHolder.suspectStatus.setText(profileStatus);
                                    Picasso.get().load(suspectImage).into(suspectViewHolder.profileImage);

                                    suspectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent singleSus = new Intent(getContext(), SuspectDetails.class);
                                            singleSus.putExtra("sus_Id",suspectIds);
                                            startActivity(singleSus);
                                        }
                                    });

//
                                }else {
                                    String profileName = dataSnapshot.child("Suspect_Name").getValue().toString();
                                    String profileProgramme = dataSnapshot.child("Suspect_Programme").getValue().toString();
                                    String profileStatus = dataSnapshot.child("Suspect_Status").getValue().toString();

                                    suspectViewHolder.suspectName.setText(profileName);
                                    suspectViewHolder.suspectProgramme.setText(profileProgramme);
                                    suspectViewHolder.suspectStatus.setText(profileStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public SuspectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suspect_view, parent, false);
                        SuspectViewHolder viewHolder = new SuspectViewHolder(view);
                        return viewHolder;
                    }
                };

                LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_animation);
                mySuspectList.setLayoutAnimation(animationController);
//                recyclerView.setAdapter(adapter);
//                adapter.startListening();

//                mySuspectList.setAdapter(adapter);
                adapter.startListening();

                Toast.makeText(getContext(),"Refreshing",Toast.LENGTH_LONG).show();
                mPullTorefreshView.postDelayed(new Runnable() {


                    @Override
                    public void run() {
                        mPullTorefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });


        return SuspectsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Suspect>()
                .setQuery(suspectReference, Suspect.class)
                .build();

        FirebaseRecyclerAdapter<Suspect, SuspectViewHolder> adapter = new FirebaseRecyclerAdapter<Suspect, SuspectViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SuspectViewHolder suspectViewHolder, int i, @NonNull Suspect suspect) {
                final String suspectIds = getRef(i).getKey();

                suspectReference.child(suspectIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("image")){
                            String suspectImage = dataSnapshot.child("image").getValue().toString();
                            String profileName = dataSnapshot.child("Suspect_Name").getValue().toString();
                            String profileProgramme = dataSnapshot.child("Suspect_Programme").getValue().toString();
                            String profileStatus = dataSnapshot.child("Suspect_Status").getValue().toString();
//                            if(dataSnapshot.child("Sus_Status").getValue().toString() == null){
//
//                            }

                            suspectViewHolder.suspectName.setText(profileName);
                            suspectViewHolder.suspectProgramme.setText(profileProgramme);
                            suspectViewHolder.suspectStatus.setText(profileStatus);
                            Picasso.get().load(suspectImage).into(suspectViewHolder.profileImage);

                            suspectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent singleSus = new Intent(getContext(), SuspectDetails.class);
                                    singleSus.putExtra("sus_Id",suspectIds);
                                    startActivity(singleSus);
                                }
                            });

//
                        }else {
                            String profileName = dataSnapshot.child("Suspect_Name").getValue().toString();
                            String profileProgramme = dataSnapshot.child("Suspect_Programme").getValue().toString();
                            String profileStatus = dataSnapshot.child("Suspect_Status").getValue().toString();

                            suspectViewHolder.suspectName.setText(profileName);
                            suspectViewHolder.suspectProgramme.setText(profileProgramme);
                            suspectViewHolder.suspectStatus.setText(profileStatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public SuspectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suspect_view, parent, false);
                SuspectViewHolder viewHolder = new SuspectViewHolder(view);
                return viewHolder;
            }
        };

        mySuspectList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class SuspectViewHolder extends RecyclerView.ViewHolder{
        TextView suspectName, suspectProgramme, suspectStatus;
        CircleImageView profileImage;

        public SuspectViewHolder(@NonNull View itemView) {
            super(itemView);

            suspectName = itemView.findViewById(R.id.sus_name);
            suspectProgramme = itemView.findViewById(R.id.sus_programme);
            suspectStatus = itemView.findViewById(R.id.sus_status);
            profileImage = itemView.findViewById(R.id.sus_img);
        }
    }
}
