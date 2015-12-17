package com.cmmakerclub.iot.cmmciotswitch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.cmmakerclub.iot.cmmciotswitch.R;
import com.cmmakerclub.iot.cmmciotswitch.helper.AppHelper;
import com.cmmakerclub.iot.cmmciotswitch.helper.BusProvider;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTOptions;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTOptions_;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTHelper;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTHelper_;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind({R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    List<ToggleButton> nameViews;

    @Bind(R.id.button10) ToggleButton masterButton;
    SparseIntArray bitMask = new SparseIntArray();

    private int mCurrentState = 0b0000;
    private Context mContext;

    @Subscribe
    public void onBusMessage(MQTTHelper.MqttEvent event) {
        switch (event.type) {
            case MQTTHelper.MqttEvent.MQTT_CONNECTING:
                Log.d(TAG, "ON-CONNECTING(line 42): ");
                break;
            case MQTTHelper.MqttEvent.MQTT_CONNECTION_LOST:
                Log.d(TAG, "ON-MQTT_CONNECTION_LOST line 45): ");
                MQTTHelper_.getInstance_(mContext).connect();
                break;
            case MQTTHelper.MqttEvent.MQTT_CONNECTED:
                Log.d(TAG, "MQTT CONNECTED (line 55)");
                String topic = MQTTOptions_.getInstance_(mContext).topic;
                Snackbar.make(findViewById(android.R.id.content), "MQTT CONNECTED to => " + topic,
                        Snackbar.LENGTH_SHORT).show();
                MQTTHelper_.getInstance_(mContext).subscribe(topic, 0);
                break;
            case MQTTHelper.MqttEvent.MQTT_SUBSCRIBED:
                Log.d(TAG, "MQTT SUBSCRIBED (line 35)");
                break;
            case MQTTHelper.MqttEvent.MQTT_MESSAGE_ARRIVED:
                String msg = event.mqttMessage.toString();
                char c = msg.charAt(msg.length()-1);
                mCurrentState = (0b0001111)&c;
                updateUI(mCurrentState);
                Log.d(TAG, "" + event.mqttMessage.toString());
                break;
            case MQTTHelper.MqttEvent.MQTT_DELIVER_COMPLETED:
                char currentText = (char) ((0b110 << 4) | mCurrentState);
                Snackbar.make(findViewById(android.R.id.content), "MESSAGE => " + currentText,
                        Snackbar.LENGTH_LONG).show();
                break;
            case MQTTHelper.MqttEvent.MQTT_CONNECT_FAIL:
                Log.d(TAG, "MQTT_CONNECT FAILED");
                Snackbar.make(findViewById(android.R.id.content), "MQTT FAILED: => " + event.reason,
                        Snackbar.LENGTH_INDEFINITE).show();
                break;
            case MQTTHelper.MqttEvent.MQTT_ERROR:
                Log.d(TAG, "ON-MESSAGE ERROR (line 62): ");
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Fabric.with(mContext, new Crashlytics());
        setContentView(R.layout.activity_main);
        BusProvider.getInstance().register(this);
        Intent intent = new Intent(mContext, ConfigurationActivity.class);
        startActivity(intent);
        ButterKnife.bind(this);

        bitMask.put(R.id.button1, 1 << 0);
        bitMask.put(R.id.button2, 1 << 1);
        bitMask.put(R.id.button3, 1 << 2);
        bitMask.put(R.id.button4, 1 << 3);
    }

    @OnClick(R.id.button10)
    public void masterChanged(ToggleButton toggleButton) {
        boolean check = toggleButton.isChecked();
        if (check) {
            mCurrentState = 0b1111;
        } else {
            mCurrentState = 0b0000;
        }
        updateUI(mCurrentState);
    }

    @OnCheckedChanged({R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    public void checkChanged(CompoundButton view, final boolean check) {
        int id = view.getId();

        if (check) {
            mCurrentState |= bitMask.get(id);
        } else {
            mCurrentState &= ~bitMask.get(id);
        }

        char state = (char) (mCurrentState&0b1111);
        masterButton.setChecked(state!=0);
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button10})
    public void buttonClicked(ToggleButton button) {
        char c = (char) ((0b110 << 4) | mCurrentState);
        Log.d(TAG, "updateUI: CHAR = " + c + " BIN = " + Integer.toBinaryString(c));
        MQTTHelper_.getInstance_(mContext).publish(String.valueOf(c), true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MQTTOptions mConOpts = MQTTOptions_.getInstance_(mContext);
        MQTTHelper_.getInstance_(mContext).setHostPort(mConOpts.host, mConOpts.port);
        MQTTHelper_.getInstance_(mContext).setTopic(mConOpts.topic);
        MQTTHelper_.getInstance_(mContext).connect();
    }

    private void updateUI(int currentState) {
        char c = (char) ((0b110 << 4) | currentState);
        ButterKnife.apply(nameViews, AppHelper.UPDATE_TOGGLE_STATE, c);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_config) {
            Intent intent = new Intent(mContext, ConfigurationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
