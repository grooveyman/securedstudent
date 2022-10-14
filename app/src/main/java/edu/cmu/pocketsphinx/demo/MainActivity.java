package edu.cmu.pocketsphinx.demo;

import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.cmu.pocketsphinx.demo.Fragments.FocusFragment;
import edu.cmu.pocketsphinx.demo.Fragments.HyperCityFragment;
import edu.cmu.pocketsphinx.demo.Fragments.HyperGhFragment;
import edu.cmu.pocketsphinx.demo.Fragments.MyJoyFragment;
import edu.cmu.pocketsphinx.demo.Fragments.SuspectFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private long backPressedTime;
    private Toast backToast;
    private RecyclerView suspectList;

    TextView navFulname,navEmail,navUsername;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDrawer = findViewById(R.id.drawerL);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();


        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navFulname = headerView.findViewById(R.id.navProfileNme);
        navUsername = headerView.findViewById(R.id.navProfileUsername);
        navEmail = headerView.findViewById(R.id.navProfileEmail);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else
            loadUserInformtion();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (menuItem.getItemId()){
                    case R.id.sus_rus:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SuspectFragment()).commit();
                        mDrawer.closeDrawers();
                        break;
                    case R.id.news_hypergh:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HyperGhFragment()).commit();
                        mDrawer.closeDrawers();
                        break;
                    case R.id.news_hypercity:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HyperCityFragment()).commit();
                        mDrawer.closeDrawers();
                        break;

                    case R.id.new_myjoy:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyJoyFragment()).commit();
                        mDrawer.closeDrawers();
                        break;
                    case R.id.news_focusfm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FocusFragment()).commit();
                        mDrawer.closeDrawers();
                        break;
//                    case R.id.menu_alert:
//                        startActivity(new Intent(getApplicationContext(), alertsActivity.class));
//                        break;

                    case R.id.menu_broadcast:
                        startActivity(new Intent(getApplicationContext(), ViewBroadcast.class));
                        break;

                }
                return true;
            }
        });



        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SuspectFragment()).commit();
            navigationView.setCheckedItem(R.id.sus_rus);
            mDrawer.closeDrawers();
        }




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StudentActivity.class));

            }
        });





    }

    private void loadUserInformtion() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());
//        String userId = dbReference.getKey();
//        assert userId != null;

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usname = dataSnapshot.child("username").getValue().toString();
                String fname = dataSnapshot.child("full_name").getValue().toString();
                navUsername.setText(usname);
                navFulname.setText(fname);

                FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
                String uemail = user.getEmail();
                navEmail.setText(uemail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public void onBackPressed() {

        if(backPressedTime+2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else {
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    public void displayLogOutDialog(){
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setTitle("Log Out");
        logoutDialog.setMessage("Are you sure you want to logout?");
        logoutDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        logoutDialog.setNegativeButton("NO",null);
        logoutDialog.setCancelable(true);
        logoutDialog.create().show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        if(item.getItemId() == R.id.logout){
            displayLogOutDialog();
        }



        return super.onOptionsItemSelected(item);
    }


}
