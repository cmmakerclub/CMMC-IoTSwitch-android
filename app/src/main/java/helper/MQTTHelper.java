package helper;

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

/**
 * Created by nat on 10/13/15 AD.
 */

@EBean(scope = EBean.Scope.Singleton)
public class MQTTHelper implements MqttCallback {
    private MqttConnectOptions mConnOpts;
    private MqttClient mClient;
    private final MqttDefaultFilePersistence dataStore;
    public static final String TAG = MQTTHelper.class.getSimpleName();
    @RootContext Context context;
    private MqttCallback mMqttCallback;
    private MQTTHelperCallback mMqttHelperCallback;

    private String clientId;
    private String password;
    private String username;
    private int port;
    private String host;

    public MQTTHelper() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir);
        mConnOpts = new MqttConnectOptions();
        mConnOpts.setCleanSession(true);
        mConnOpts.setKeepAliveInterval(10);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        mMqttCallback.connectionLost(throwable);
        try {
            throw throwable;
        } catch (Throwable throwable1) {
            throwable1.printStackTrace();
        }

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        mqttMessage.setQos(0);
        mMqttCallback.messageArrived(s, mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        mMqttCallback.deliveryComplete(iMqttDeliveryToken);
    }

    @Background
    public void createConnection(MQTTHelperCallback callback) {
        Log.d(TAG, "createConnection (line ): ");
        mMqttHelperCallback = callback;
        try {
            String host = "tcp://" + this.host + ":" + this.port;
            Log.d(TAG, "clientId " + this.clientId + " " + host);
            Log.d(TAG, "createConnection: " + host);
            mClient = new MqttClient(host, this.clientId, dataStore);
            mClient.connect(mConnOpts);
            mClient.setCallback(this);
            Log.d(TAG, "calling onReady (line 77): ");
            mMqttHelperCallback.onReady(mClient);
        } catch (MqttException e) {
            mMqttHelperCallback.onError(e);
            e.printStackTrace();
        }
    }

    @Background
    public void subscribe(String topic, int qos, MqttCallback callback) {
        Log.d(TAG, "subscribe topic = " + topic);
        if (callback != null) {
            mMqttCallback = callback;
        }

        try {
            mClient.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

//    public void connectMqtt(MqttCallback mqttCallback) {
//        try {
//            String host = "tcp://" + this.host + ":" + this.password;
//            mClient = new MqttClient(host, this.clientId, dataStore);
//            mClient.setCallback(this);
//        } catch (MqttException e) {
//            mMqttHelperCallback.onError(e);
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "connect (line ): ");
//        this.mMqttCallback = mqttCallback;
//        try {
//            mClient.connect(mConnOpts);
//            mClient.setTimeToWait(500);
//        } catch (MqttException e) {
//            mMqttHelperCallback.onError(e);
//            e.printStackTrace();
//        }
//    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAuth(String user, String pass) {
        // do notthing if no user/pass provided
        if (user.isEmpty() || pass.isEmpty()) {
            return;
        }

        this.username = user;
        this.password = pass;

        try {
            mConnOpts.setUserName(this.username);
            mConnOpts.setPassword(this.password.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            mMqttHelperCallback.onUnkownError(ex);
        }

    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

//    @Background
//    public void reconnect() {
//        try {
//            if (mClient.isConnected()) {
//                mClient.disconnect();
//            }
//            mClient.connect(mConnOpts);
//            String topic = String.format("%s/%s/status", mDevice.prefix, mDevice.getTarget());
//            Log.d(TAG, "topic = " + topic);
//            mClient.subscribe(topic, 0);
//        } catch (MqttException e) {
//            mMqttHelperCallback.onError(e);
//            e.printStackTrace();
//        }
//    }

    public static interface MQTTHelperCallback {
        public void onReady(MqttClient mqttClient);

        public void onError(MqttException e);

        public void onUnkownError(Exception e);
    }
}
