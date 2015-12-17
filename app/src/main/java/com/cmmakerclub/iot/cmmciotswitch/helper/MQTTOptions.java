package com.cmmakerclub.iot.cmmciotswitch.helper;

import android.content.Context;

import com.cmmakerclub.iot.cmmciotswitch.Constants;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.UUID;

/**
 * Created by nat on 12/17/15 AD.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MQTTOptions {

    public String clientId;
    public String password;
    public String username;
    public int port;
    public String host;
    public String topic;

    @RootContext
    public Context mContext;


    public MQTTOptions() {
    }

    public MQTTOptions reloadConfig() {
        clientId = AppHelper.getString(mContext, Constants.MQTT_CLIENT_ID,
                UUID.randomUUID().toString().split("-")[0]);
        username = AppHelper.getString(mContext, Constants.MQTT_USERNAME, "");
        password = AppHelper.getString(mContext, Constants.MQTT_PASSWORD, "");
        port = Integer.parseInt(AppHelper.getString(mContext, Constants.MQTT_PORT, "1883"));
        host = AppHelper.getString(mContext, Constants.MQTT_HOST, "mqtt.espert.io");
        topic = AppHelper.getString(mContext, Constants.MQTT_TOPIC, "test");
        return this;
    }
}
