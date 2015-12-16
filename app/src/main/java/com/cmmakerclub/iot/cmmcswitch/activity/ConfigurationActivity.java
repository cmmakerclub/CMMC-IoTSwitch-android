package com.cmmakerclub.iot.cmmcswitch.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cmmakerclub.iot.cmmcswitch.Constants;
import com.cmmakerclub.iot.cmmcswitch.R;
import com.cmmakerclub.iot.cmmcswitch.databinding.ActivityConfigurationBinding;
import com.cmmakerclub.iot.cmmcswitch.databinding.ContentConfigurationBinding;
import com.cmmakerclub.iot.cmmcswitch.helper.AppHelper;
import com.cmmakerclub.iot.cmmcswitch.model.ViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity {
    ActivityConfigurationBinding mBinding;
    ContentConfigurationBinding cc;

    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.host_edittext) EditText mHostEditText;
    @Bind(R.id.topic_edittext) EditText mTopicEditText;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_configuration);

        mContext = this;
        ButterKnife.bind(this);

        String host = AppHelper.getString(mContext, Constants.MQTT_HOST, "");
        String topic = AppHelper.getString(mContext, Constants.MQTT_TOPIC, "");

        ViewModel.MqttConfig mqttConfig = new ViewModel.MqttConfig(host, topic);
        mBinding.setConfig(mqttConfig);
        setSupportActionBar(mToolBar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                finish();
            }
        });
    }
    private void attemptLogin() {
        // Reset errors.
        mHostEditText.setError(null);

        // Store values at the time of the login attempt.
        String host = mHostEditText.getText().toString();
        String topic= mTopicEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(topic)) {
            mTopicEditText.setError(getString(R.string.error_field_required));
            focusView = mTopicEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(host)) {
            mHostEditText.setError(getString(R.string.error_field_required));
            focusView = mHostEditText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
        }
    }

}
