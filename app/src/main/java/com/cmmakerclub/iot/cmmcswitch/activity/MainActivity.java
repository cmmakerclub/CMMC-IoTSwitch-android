package com.cmmakerclub.iot.cmmcswitch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.cmmakerclub.iot.cmmcswitch.R;
import com.cmmakerclub.iot.cmmcswitch.helper.AppHelper;
import com.cmmakerclub.iot.cmmcswitch.helper.BusProvider;
import com.cmmakerclub.iot.cmmcswitch.helper.MQTTHelper;
import com.cmmakerclub.iot.cmmcswitch.helper.MQTTHelper_;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
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
                MQTTHelper_.getInstance_(mContext).subscribe("hello", 0);
                break;
            case MQTTHelper.MqttEvent.MQTT_SUBSCRIBED:
                Log.d(TAG, "MQTT SUBSCRIBED (line 35)");
                break;
            case MQTTHelper.MqttEvent.MQTT_MESSAGE_ARRIVED:
                String msg = event.mqttMessage.toString();
                char c = msg.charAt(msg.length()-1);
                mCurrentState = (0b111<<4)|c;
                updateUI(mCurrentState);
                Log.d(TAG, "" + event.mqttMessage.toString());
                break;
            case MQTTHelper.MqttEvent.MQTT_DELIVER_COMPLETED:
                Log.d(TAG, "ON-MESSAGE DELIVER (line 59): ");
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

        masterButton.setChecked(mCurrentState!=0);
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button10})
    public void buttonClicked(ToggleButton button) {
        char c = (char) ((0b110 << 4) | mCurrentState);
        Log.d(TAG, "updateUI: CHAR = " + c);
        MQTTHelper_.getInstance_(mContext).publish(String.valueOf(c), true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MQTTHelper_.getInstance_(mContext).setHostPort("cmmc.xyz", 1883);
        MQTTHelper_.getInstance_(mContext).setTopic("hello");
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
}
