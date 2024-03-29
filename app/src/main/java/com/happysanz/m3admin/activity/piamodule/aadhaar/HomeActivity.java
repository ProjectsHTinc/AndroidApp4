package com.happysanz.m3admin.activity.piamodule.aadhaar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.piamodule.AddCandidateActivity;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.happysanz.m3admin.utils.aadhaar.DataAttributes;
import com.happysanz.m3admin.utils.aadhaar.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class HomeActivity extends AppCompatActivity {

    // variables to store extracted xml data
    String uid, name, gender, yearOfBirth, careOf, house, street, location, villageTehsil, postOffice, district, state, postCode, dob;

    // UI Elements
    TextView tv_sd_uid, tv_sd_name, tv_sd_gender, tv_sd_yob, tv_sd_dob, tv_sd_co, tv_sd_vtc, tv_sd_po, tv_sd_dist,
            tv_sd_state, tv_sd_pc, tv_cancel_action;
    LinearLayout ll_scanned_data_wrapper, ll_data_wrapper, ll_action_button_wrapper;
    private ImageView imBack;

    // Storage
    Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the default action bar
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        // init the UI Elements
        tv_sd_uid = (TextView) findViewById(R.id.tv_sd_uid);
        tv_sd_name = (TextView) findViewById(R.id.tv_sd_name);
        tv_sd_gender = (TextView) findViewById(R.id.tv_sd_gender);
        tv_sd_yob = (TextView) findViewById(R.id.tv_sd_yob);
        tv_sd_dob = (TextView) findViewById(R.id.tv_sd_dob);
        tv_sd_co = (TextView) findViewById(R.id.tv_sd_co);
        tv_sd_vtc = (TextView) findViewById(R.id.tv_sd_vtc);
        tv_sd_po = (TextView) findViewById(R.id.tv_sd_po);
        tv_sd_dist = (TextView) findViewById(R.id.tv_sd_dist);
        tv_sd_state = (TextView) findViewById(R.id.tv_sd_state);
        tv_sd_pc = (TextView) findViewById(R.id.tv_sd_pc);
        tv_cancel_action = (TextView) findViewById(R.id.tv_cancel_action);

        ll_scanned_data_wrapper = (LinearLayout) findViewById(R.id.ll_scanned_data_wrapper);
        ll_data_wrapper = (LinearLayout) findViewById(R.id.ll_data_wrapper);
        ll_action_button_wrapper = (LinearLayout) findViewById(R.id.ll_action_button_wrapper);

        imBack = findViewById(R.id.back_tic_his);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //init storage
        storage = new Storage(this);
    }

    /**
     * onclick handler for scan new card
     *
     * @param view
     */
    public void scanNow(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a Aadharcard QR Code");
//        integrator.setResultDisplayDuration(500);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            // process received data
            if (scanContent != null && !scanContent.isEmpty()) {
                processScannedData(scanContent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * process xml string received from aadhaar card QR code
     *
     * @param scanData
     */
    protected void processScannedData(String scanData) {
        Log.d("Rajdeol", scanData);
        XmlPullParserFactory pullParserFactory;

        try {
            // init the parserfactory
            pullParserFactory = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));

            // parse the XML
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("Rajdeol", "Start document");
                } else if (eventType == XmlPullParser.START_TAG && DataAttributes.AADHAAR_DATA_TAG.equals(parser.getName())) {
                    // extract data from tag
                    //uid
                    uid = parser.getAttributeValue(null, DataAttributes.AADHAR_UID_ATTR);
                    //name
                    name = parser.getAttributeValue(null, DataAttributes.AADHAR_NAME_ATTR);
                    //gender
                    gender = parser.getAttributeValue(null, DataAttributes.AADHAR_GENDER_ATTR);
                    // year of birth
                    yearOfBirth = parser.getAttributeValue(null, DataAttributes.AADHAR_YOB_ATTR);
                    // care of
                    careOf = parser.getAttributeValue(null, DataAttributes.AADHAR_CO_ATTR);
                    // house
                    house = parser.getAttributeValue(null, DataAttributes.AADHAR_HOUSE);
                    // street
                    street = parser.getAttributeValue(null, DataAttributes.AADHAR_STREET);
                    // location
                    location = parser.getAttributeValue(null, DataAttributes.AADHAR_LOCATION);
                    // village Tehsil
                    villageTehsil = parser.getAttributeValue(null, DataAttributes.AADHAR_VTC_ATTR);
                    // Post Office
                    postOffice = parser.getAttributeValue(null, DataAttributes.AADHAR_PO_ATTR);
                    // district
                    district = parser.getAttributeValue(null, DataAttributes.AADHAR_DIST_ATTR);
                    // state
                    state = parser.getAttributeValue(null, DataAttributes.AADHAR_STATE_ATTR);
                    // Post Code
                    postCode = parser.getAttributeValue(null, DataAttributes.AADHAR_PC_ATTR);
                    // date of birth
                    dob = parser.getAttributeValue(null, DataAttributes.AADHAR_DOB);

                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("Rajdeol", "End tag " + parser.getName());

                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("Rajdeol", "Text " + parser.getText());

                }
                // update eventType
                eventType = parser.next();
            }

            // display the data on screen
            displayScannedData();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }// EO function

    /**
     * show scanned information
     */
    public void displayScannedData() {
        ll_data_wrapper.setVisibility(View.GONE);
        ll_scanned_data_wrapper.setVisibility(View.VISIBLE);
        ll_action_button_wrapper.setVisibility(View.VISIBLE);

        // clear old values if any
        tv_sd_uid.setText("");
        tv_sd_name.setText("");
        tv_sd_gender.setText("");
        tv_sd_yob.setText("");
        tv_sd_dob.setText("");
        tv_sd_co.setText("");
        tv_sd_vtc.setText("");
        tv_sd_po.setText("");
        tv_sd_dist.setText("");
        tv_sd_state.setText("");
        tv_sd_pc.setText("");

        // update UI Elements
        tv_sd_uid.setText(uid);
        tv_sd_name.setText(name);
        tv_sd_gender.setText(gender);
        tv_sd_yob.setText(yearOfBirth);
        tv_sd_dob.setText(dob);
        tv_sd_co.setText(careOf);
        tv_sd_vtc.setText(villageTehsil);
        tv_sd_po.setText(postOffice);
        tv_sd_dist.setText(district);
        tv_sd_state.setText(state);
        tv_sd_pc.setText(postCode);
    }

    /**
     * display home screen onclick listener for cancel button
     *
     * @param view
     */
    public void showHome(View view) {
        ll_data_wrapper.setVisibility(View.VISIBLE);
        ll_scanned_data_wrapper.setVisibility(View.GONE);
        ll_action_button_wrapper.setVisibility(View.GONE);
    }

    /**
     * save data to storage
     */
    public void saveData(View view) {
        // We are going to use json to save our data
        // create json object
        JSONObject aadhaarData = new JSONObject();
        try {
            aadhaarData.put(DataAttributes.AADHAR_UID_ATTR, uid);

//            deleteCard(uid);

            if (name == null) {
                name = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_NAME_ATTR, name);

            if (gender == null) {
                gender = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_GENDER_ATTR, gender);

            if (yearOfBirth == null) {
                yearOfBirth = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_YOB_ATTR, yearOfBirth);

            if (house == null) {
                house = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_HOUSE, house);

            if (street == null) {
                street = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_STREET, street);

            if (location == null) {
                location = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_LOCATION, location);

            if (careOf == null) {
                careOf = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_CO_ATTR, careOf);

            if (villageTehsil == null) {
                villageTehsil = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_VTC_ATTR, villageTehsil);

            if (postOffice == null) {
                postOffice = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_PO_ATTR, postOffice);

            if (district == null) {
                district = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_DIST_ATTR, district);

            if (state == null) {
                state = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_STATE_ATTR, state);

            if (postCode == null) {
                postCode = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_PC_ATTR, postCode);

            if (dob == null) {
                dob = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_DOB, dob);


            // read data from storage
            String storageData = storage.readFromFile();

            JSONArray storageDataArray;
            //check if file is empty
            if (storageData.length() > 0) {
                storageDataArray = new JSONArray(storageData);
            } else {
                storageDataArray = new JSONArray();
            }


            // check if storage is empty
            if (storageDataArray.length() > 0) {
                // check if data already exists
                for (int i = 0; i < storageDataArray.length(); i++) {
                    String dataUid = storageDataArray.getJSONObject(i).getString(DataAttributes.AADHAR_UID_ATTR);
                    if (uid.equals(dataUid)) {
                        // do not save anything and go back
                        // show home screen
//                        tv_cancel_action.performClick();
                        Intent intent = new Intent(this, AddCandidateActivity.class);
                        PreferenceStorage.saveAadhaarAction(getApplicationContext(), "aadhaar");
                        // Start Activity
                        startActivity(intent);
                        finish();

                        return;
                    }
                }
            }
            // add the aadhaar data
            storageDataArray.put(aadhaarData);
            // save the aadhaardata
            storage.writeToFile(storageDataArray.toString());

            // intent for SavedAadhaarcardActivity
            Intent intent = new Intent(this, AddCandidateActivity.class);
            PreferenceStorage.saveAadhaarAction(getApplicationContext(), "aadhaar");
            // Start Activity
            startActivity(intent);
            finish();

            // show home screen
//            tv_cancel_action.performClick();


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * onclick handler for show saved cards
     * this will start the SavedAadhaarCardActivity
     *
     * @param view
     */
    public void showSavedCards(View view) {
        // intent for SavedAadhaarcardActivity
        Intent intent = new Intent(this, SavedAadhaarCardActivity.class);
        // Start Activity
        startActivity(intent);
    }

    /**
     * delete saved aadhaar card
     *
     * @param uid
     */
    public void deleteCard(String uid) {
        // read data from storage
        String storageData = storage.readFromFile();

        JSONArray storageDataArray;
        //check if file is empty
        if (storageData.length() > 0) {
            try {
                storageDataArray = new JSONArray(storageData);
                // coz I am working on Android version which doesnot support remove method on JSONArray
                JSONArray updatedStorageDataArray = new JSONArray();

                // check if data already exists
                for (int i = 0; i < storageDataArray.length(); i++) {
                    String dataUid = storageDataArray.getJSONObject(i).getString(DataAttributes.AADHAR_UID_ATTR);
                    if (!uid.equals(dataUid)) {
                        updatedStorageDataArray.put(storageDataArray.getJSONObject(i));
                    }
                }

                // save the updated list
                storage.writeToFile(updatedStorageDataArray.toString());

                // Hide the list if all cards are deleted
                if (updatedStorageDataArray.length() < 1) {
                    // hide list and show message
//                    tv_no_saved_card.setVisibility(View.VISIBLE);
//                    lv_saved_card_list.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}// EO class
