package com.cmmakerclub.iot.cmmcswitch.helper;

import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.UUID;

/**
 * Created by nat on 10/13/15 AD.
 */

@EBean(scope = EBean.Scope.Singleton)
public class MQTTHelper implements MqttCallback {

    final private MqttConnectOptions mConnOpts;

    private final MqttDefaultFilePersistence dataStore;
    public static final String TAG = MQTTHelper.class.getSimpleName();
    @RootContext
    Context context;

    private MqttClient mClient;
    private String clientId;
    private String password;
    private String username;
    private int port;
    private String host;
    private int mQos = 0;
    private String mTopic;


    public MQTTHelper() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir);
        mConnOpts = new MqttConnectOptions();
        mConnOpts.setCleanSession(true);
        mConnOpts.setKeepAliveInterval(10);
        clientId = UUID.randomUUID().toString().split("-")[0];
    }

    @Override
    public void connectionLost(Throwable throwable) {
        BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_CONNECTION_LOST));
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        MqttEvent event = new MqttEvent();
        event.type = MqttEvent.MQTT_MESSAGE_ARRIVED;
        event.mqttMessage = mqttMessage;
        BusProvider.getInstance().postQueue(event);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_DELIVER_COMPLETED));
    }

    @Background
    public void subscribe(String topic, int qos) {
        try {
            mClient.subscribe(topic, qos);
            Log.d(TAG, "subscribe topic = " + topic);
            BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_SUBSCRIBED));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void _publish(String topic, String message, int qos, boolean retain) throws MqttException {
        BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_PUBLISHING));
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retain);
        mqttMessage.setPayload(message.getBytes());
        mClient.publish(topic, mqttMessage);
    }

    @Background
    public void publish(String msg, boolean retain) {
        try {
            _publish(this.mTopic, msg, mQos, retain);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Background
    public void connect() {
        BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_CONNECTING));
        if (mClient.isConnected()) {
            BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_CONNECTED));
        }
        else {
            try {
                mClient.connect(mConnOpts);
                BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_CONNECTED));
            } catch (MqttException e) {
                BusProvider.getInstance().postQueue(new MqttEvent(MqttEvent.MQTT_CONNECT_FAIL));
                e.printStackTrace();
            }
        }
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
        createConnection();
    }

    public void setAuth(String user, String pass) {
        if (user.isEmpty() || pass.isEmpty()) {
            return;
        }

        this.username = user;
        this.password = pass;

        mConnOpts.setUserName(this.username);
        mConnOpts.setPassword(this.password.toCharArray());
        createConnection();
    }

    private void createConnection() {
        String host = "tcp://" + this.host + ":" + this.port;
        Log.d(TAG, "clientId " + this.clientId + " " + host);
        Log.d(TAG, "prepareConnection: " + host);
        try {
            mClient = new MqttClient(host, this.clientId, dataStore);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mClient.setCallback(this);
    }

    public void setHostPort(String host, int port) {
        this.host = host;
        this.port = port;
        createConnection();
    }

    public void setTopic(String topic) {
        this.mTopic = topic;
    }

    public static class MqttEvent {
        public static final int MQTT_CONNECTED = 0x00;
        public static final int MQTT_MESSAGE_ARRIVED = 0x01;
        public static final int MQTT_CONNECTION_LOST = 0x02;
        public static final int MQTT_DELIVER_COMPLETED = 0x03;
        public static final int MQTT_ERROR = 0x04;
        public static final int MQTT_CONNECT_FAIL = 0x05;
        public static final int MQTT_CONNECTING = 0x06;
        public static final int MQTT_PUBLISHING = 0x07;
        public static final int MQTT_SUBSCRIBED = 0x08;
        public int type;

        public MqttMessage mqttMessage;

        public MqttEvent(int type) {
            this.type = type;
        }

        public MqttEvent() {

        }
    }
}
