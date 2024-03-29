package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.MobilizerListAdapter;
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.bean.pia.MobilizerList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MobilizerActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = MobilizerActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    ArrayList<Mobilizer> mobilizerArrayList;
    MobilizerListAdapter mobilizerListAdapter;
    ListView loadMoreListView;
    Mobilizer pia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilizer);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewUserActivity.class);
                intent.putExtra("page", "mob");
                startActivity(intent);
                finish();
            }
        });

        mobilizerArrayList = new ArrayList<>();
        loadMoreListView = (ListView) findViewById(R.id.list_mobilizers);
        loadMoreListView.setOnItemClickListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra("eventObj")) {
            pia = (Mobilizer) intent.getSerializableExtra("eventObj");
            PreferenceStorage.saveUserId(this, pia.getUser_id());
            callGetClassTestService();
        } else {
            callGetClassTestService();
        }
        if (PreferenceStorage.getTnsrlmCheck(this)){
            findViewById(R.id.add_user).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            Log.d("ajazFilterresponse : ", response.toString());

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    Gson gson = new Gson();
                    MobilizerList mobilizerList = gson.fromJson(response.toString(), MobilizerList.class);
                    if (mobilizerList.getMobilizerArrayList() != null && mobilizerList.getMobilizerArrayList().size() > 0) {
                        totalCount = mobilizerList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(mobilizerList.getMobilizerArrayList());
                    }
                    else {
                        if (mobilizerArrayList != null) {
                            mobilizerArrayList.clear();
                            updateListAdapter(mobilizerList.getMobilizerArrayList());
                        }
                    }
                }
            });
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }

    public void callGetClassTestService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadMob();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        Mobilizer userData = null;
        if ((mobilizerListAdapter != null) && (mobilizerListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = mobilizerListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            userData = mobilizerArrayList.get(actualindex);
        } else {
            userData = mobilizerArrayList.get(position);
        }
        if (PreferenceStorage.getTnsrlmCheck(this)){
            userData = mobilizerArrayList.get(position);
        } else {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            intent.putExtra("userObj", userData);
            intent.putExtra("page", "mob");
            PreferenceStorage.saveMobId(this, userData.getUser_id());
            PreferenceStorage.saveMobName(this, userData.getName());
            startActivity(intent);
            finish();
        }
    }

    private void loadMob() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getPIAProfileId(getApplicationContext());
        } else {
            id = PreferenceStorage.getUserId(getApplicationContext());
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_PIA_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_MOBILIZER_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }


    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    protected void updateListAdapter(ArrayList<Mobilizer> mobilizerArrayList) {
        this.mobilizerArrayList.addAll(mobilizerArrayList);
        if (mobilizerListAdapter == null) {
            mobilizerListAdapter = new MobilizerListAdapter(this, this.mobilizerArrayList);
            loadMoreListView.setAdapter(mobilizerListAdapter);
        } else {
            mobilizerListAdapter.notifyDataSetChanged();
        }
    }

}