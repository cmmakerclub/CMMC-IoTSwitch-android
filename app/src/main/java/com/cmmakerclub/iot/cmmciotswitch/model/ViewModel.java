package com.cmmakerclub.iot.cmmciotswitch.model;

import android.content.Context;

import com.cmmakerclub.iot.cmmciotswitch.BuildConfig;
import com.cmmakerclub.iot.cmmciotswitch.helper.MQTTOptions_;

import java.util.UUID;

/**
 * Created by nat on 12/1/15 AD.
 */
public class ViewModel {

    public static class MqttConfig {
        public String host;
        public String port;
        public String topic;
        public String clientId;
        final public String versionCode;

        public MqttConfig(Context context) {
            this.clientId = "CMMC-" + UUID.randomUUID().toString().split("-")[0];
            this.versionCode = "versionCode: " +BuildConfig.VERSION_CODE;
            host =  MQTTOptions_.getInstance_(context).host;
            port =  String.valueOf(MQTTOptions_.getInstance_(context).port);
            topic = MQTTOptions_.getInstance_(context).topic;
            clientId = MQTTOptions_.getInstance_(context).clientId;
        }
    }
}
