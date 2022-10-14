/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class StudentActivity extends Activity implements RecognitionListener {

    FirebaseAuth firebaseAuth;
    String firebaseUser ;
    DatabaseReference databaseReference;



    final ArrayList<String> list = new ArrayList<>();

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;

    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";




    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private String KEYPHRASE = "activate";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);


        firebaseAuth = FirebaseAuth.getInstance();


            firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser);


        mRequestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("security");






        //Retrieve user data
         retrieve_user_info();







        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                edu.cmu.pocketsphinx.demo.location information = new location(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("location");

                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"location Sent", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(getApplicationContext(),"location failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                Toast.makeText(getApplicationContext(),location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();



        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        //noinspection MissingPermission


        // Prepare the data for UI
        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);

        setContentView(R.layout.main);

        ((TextView) findViewById(R.id.caption_text))
                .setText("To trigger an alert say \"Activate");




        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new SetupTask(this).execute();


    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<StudentActivity> activityReference;

        SetupTask(StudentActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.caption_text))
                        .setText("Failed to init recognizer " + result);
            } else {
                activityReference.get().switchSearch(KWS_SEARCH);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode){
            case PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Recognizer initialization is a time-consuming and it involves IO,
                    // so we execute it in async task
                    new SetupTask(this).execute();
                } else {
                    finish();
                }
                break;
            case 10:
                configure_button();
            default:
                break;
        }


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
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }











    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();


        //check if recorded text is same as the alert keyphrase

        if (text.equals(KEYPHRASE)) {

            switchSearch(MENU_SEARCH);

        } else{

            ((TextView) findViewById(R.id.result_text)).setText(text);

        }


    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {

        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            switch (text){
                case "extortion":
                    triggerMessage("Student Extortion in progress");
                    Toast.makeText(getApplicationContext(),"Extortion Alert sent", Toast.LENGTH_SHORT).show();
                    break;
                case "rape":
                    triggerMessage("Rape in progress");
                    Toast.makeText(getApplicationContext(),"Rape Alert sent", Toast.LENGTH_SHORT).show();
                    break;
                case "break in":
                    triggerMessage("Residence break in in progress");
                    Toast.makeText(getApplicationContext(),"Break in Alert sent", Toast.LENGTH_SHORT).show();
                    break;
                case "riot":
                    triggerMessage("Riot in progress");
                    Toast.makeText(getApplicationContext(),"Riot Alert sent", Toast.LENGTH_SHORT).show();
                    sendNotification();
                    break;
                default:
                    return;

            }


        }

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH)){
            switchSearch(KWS_SEARCH);}
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            Toast.makeText(getApplicationContext(), "Listening!!!!!!!!!", Toast.LENGTH_SHORT).show();
            recognizer.startListening(searchName);
        }
        else {
//            Toast.makeText(getApplicationContext(), "Listening!!!!!!!!!", Toast.LENGTH_SHORT).show();
            recognizer.startListening(searchName, 10000);

            String caption = getResources().getString(captions.get(searchName));
            ((TextView) findViewById(R.id.caption_text)).setText(caption);
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }



    //function to retrieve user information
    public void retrieve_user_info(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error! "+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }




    //function to send alert message
    public void triggerMessage(final String message){



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("alerts");
        DatabaseReference newalert = myRef.push();




        newalert.child("username").setValue(list.get(1));
        newalert.child("full_name").setValue(list.get(4));
        newalert.child("ref_number").setValue(list.get(3));
        newalert.child("gender").setValue(list.get(2));
        newalert.child("message").setValue(message);
        newalert.child("user_uid").setValue(firebaseUser);





        //send alert in here
//        alert information = new alert(list.get(1), list.get(4), list.get(3), list.get(2),firebaseUser,message);
//
//        FirebaseDatabase.getInstance().getReference("alerts")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(StudentActivity.this, "Alert Successfully sent", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(StudentActivity.this, "Alert transmission failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



    }



    private void sendNotification() {
        //json object
        JSONObject mainObj = new JSONObject();
        try{

            mainObj.put("to", "/topics/"+"security");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title","an alert");
            notificationObj.put("body","An alert was triggered");

            JSONObject extraData = new JSONObject();
            extraData.put("brandId","apb");
            extraData.put("category","alerts");
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


}
