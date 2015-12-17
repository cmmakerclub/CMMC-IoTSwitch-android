package com.cmmakerclub.iot.cmmciotswitch.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.cmmakerclub.iot.cmmciotswitch.Constants;
import com.cmmakerclub.iot.cmmciotswitch.R;
import com.cmmakerclub.iot.cmmciotswitch.databinding.ActivityConfigurationBinding;
import com.cmmakerclub.iot.cmmciotswitch.databinding.ContentConfigurationBinding;
import com.cmmakerclub.iot.cmmciotswitch.helper.AppHelper;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTOptions;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTOptions_;
import com.cmmakerclub.iot.cmmciotswitch.model.ViewModel;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class ConfigurationActivity extends BaseActivity {
    ActivityConfigurationBinding mBinding;
    ContentConfigurationBinding cc;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.host_edittext)
    EditText mHostEditText;
    @Bind(R.id.topic_edittext)
    EditText mTopicEditText;
    @Bind(R.id.port_edittext)
    EditText mPortEditText;
    @Bind(R.id.clientid_edittext)
    EditText mClientIdEditText;

    private Context mContext;

    MQTTOptions mConnOpts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_configuration);

        mContext = this;
        ButterKnife.bind(this);
        mConnOpts = MQTTOptions_.getInstance_(mContext).reloadConfig();


        mBinding.setConfig(new ViewModel.MqttConfig(mContext));
        setSupportActionBar(mToolBar);
    }

    private boolean validateForm() {
        // Reset errors.
        mHostEditText.setError(null);

        // Store values at the time of the login attempt.
        String host = mHostEditText.getText().toString();
        String topic = mTopicEditText.getText().toString();
        String port = mPortEditText.getText().toString();
        String clientId = mClientIdEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(clientId)) {
            mClientIdEditText.setError(getString(R.string.error_field_required));
            focusView = mClientIdEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(topic)) {
            mTopicEditText.setError(getString(R.string.error_field_required));
            focusView = mTopicEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(port)) {
            mPortEditText.setError(getString(R.string.error_field_required));
            focusView = mPortEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(host)) {
            mHostEditText.setError(getString(R.string.error_field_required));
            focusView = mHostEditText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            AppHelper.setString(mContext, Constants.MQTT_HOST, host);
            AppHelper.setString(mContext, Constants.MQTT_PORT, port);
            AppHelper.setString(mContext, Constants.MQTT_CLIENT_ID, clientId);
            AppHelper.setString(mContext, Constants.MQTT_TOPIC, topic);
            MQTTOptions_.getInstance_(mContext).reloadConfig();
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (validateForm()) {
                finish();
            } else {

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnLongClick(R.id.clientid_edittext)
    public boolean generateClientId(EditText editTextView) {
        AppHelper.setString(editTextView.getContext(), Constants.MQTT_CLIENT_ID,
                "CMMC-"+UUID.randomUUID().toString().split("-")[0]);
        MQTTOptions_.getInstance_(editTextView.getContext()).reloadConfig();
        mBinding.setConfig(new ViewModel.MqttConfig(mContext));

        return true;
    }
}
