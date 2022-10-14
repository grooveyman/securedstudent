package edu.cmu.pocketsphinx.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class broadcastActivity extends AppCompatActivity {

    EditText tittle,message;
    Button send_message;
    ProgressBar progressBar;
    DatabaseReference databaseReference;


    Notification notification;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;

    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        notification  = new Notification(this);

        getSupportActionBar().setTitle("Broadcast Information");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tittle = findViewById(R.id.tittle);
        message = findViewById(R.id.b_message);
        send_message = findViewById(R.id.send_broadcast_message);
        progressBar = findViewById(R.id.progressBar);

        mRequestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("security");

        if (getIntent().hasExtra("category") && getIntent().getStringExtra("category").equals("broadcast")){
            Intent intent = new Intent(broadcastActivity.this, ViewBroadcast.class);
            intent.putExtra("category",getIntent().getStringExtra("category"));
            intent.putExtra("brandId",getIntent().getStringExtra("brandId"));
            startActivity(intent);
        }




        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(tittle.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Title field can't be empty", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(message.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Message field cant be empty", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    NOTIFICATION_TITLE = tittle.getText().toString().trim();
                    NOTIFICATION_MESSAGE = message.getText().toString().trim();

//                    FirebaseDatabase.getInstance().getReference().child("Tokens").child("OkRcv4JpDpcKNvpvJtaV60U4tDl2\n").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String userToken = dataSnapshot.getValue(String.class);
////                            sendNotifications(userToken,"Trial","This is a test notification");
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    broadcast information = new broadcast(tittle.getText().toString(),message.getText().toString());

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("broadcasts");
                    DatabaseReference newbroadcast = myRef.push();



                    newbroadcast.setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendNotification();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"broadcast Sent",Toast.LENGTH_SHORT).show();
                                tittle.getText().clear();
                                message.getText().clear();
                                startActivity(new Intent(getApplicationContext(), ViewBroadcast.class));

                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"broadcast failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });


    }

    private void sendNotification() {
        //json object
        JSONObject mainObj = new JSONObject();
        try{

            mainObj.put("to", "/topics/"+"security");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",NOTIFICATION_TITLE);
            notificationObj.put("body",NOTIFICATION_MESSAGE);

            JSONObject extraData = new JSONObject();
            extraData.put("brandId","apb");
            extraData.put("category","broadcast");
            mainObj.put("notification", notificationObj);
            mainObj.put("data",extraData);



            //create json object request

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObj,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAO3wp-Uk:APA91bGtF2n94JnfiJ5yFbtdVqB3lBJ62zIMa4WW3HKziYRyny9erjiVAiVznqElvrn2RpcrWVEQyxDUtvLDVwKNkJOh1DQL1XZ8yw_PGKQdheiUpt1cTBh2-SJLIEy2t6VVgd68ctEh");
                    return header;
                }
            };

            mRequestQueue.add(request);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

//    public void tell_user(String Tittle,String Message){
//        final String CHANNEL_ID ="Notificatioon" ;
//
//        //check os version
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Broadcast", importance);
//            channel.setDescription("All is well");
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//
//
//
//        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(this, ViewBroadcast.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notifications)
//                .setContentTitle(Tittle)
//                .setContentText(Message)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(999,builder.build());
//
//
//
//    }
//
//    public  void updateToken(){
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        String refreshToken = FirebaseInstanceId.getInstance().getToken();
//        Token token = new Token(refreshToken);
//        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
//    }
//
//    public void sendNotifications(String usertoken, String title, String message){
//        Data data = new Data(title, message);
//        NotificationSender sender = new NotificationSender(data, usertoken);
//        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
//            @Override
//            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                if(response.code() == 200){
//                    if(response.body().success != 1){
//                        Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MyResponse> call, Throwable t) {
//
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == item.getItemId()){
            //end Activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
