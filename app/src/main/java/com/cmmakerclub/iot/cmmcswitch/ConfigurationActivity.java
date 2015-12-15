package com.cmmakerclub.iot.cmmcswitch;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cmmakerclub.iot.cmmcswitch.databinding.ActivityConfigurationBinding;
import com.cmmakerclub.iot.cmmcswitch.databinding.ContentConfigurationBinding;
import com.cmmakerclub.iot.cmmcswitch.model.ViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity {
    ActivityConfigurationBinding mBinding;
    ContentConfigurationBinding cc;

    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.fab) FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_configuration);

        ButterKnife.bind(this);

        ViewModel.MqttConfig mqttConfig = new ViewModel.MqttConfig("iot.eclipse.org");
        mBinding.setConfig(mqttConfig);
        setSupportActionBar(mToolBar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });
    }

}
