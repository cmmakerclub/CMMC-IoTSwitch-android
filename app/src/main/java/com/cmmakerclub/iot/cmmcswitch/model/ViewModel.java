package com.cmmakerclub.iot.cmmcswitch.model;

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

        public MqttConfig(String host, String topic) {
            this.host = host;
            this.port = "1883";
            this.topic = topic;
            this.clientId = "CMMC-" + UUID.randomUUID().toString().split("-")[0];
        }
    }
}
