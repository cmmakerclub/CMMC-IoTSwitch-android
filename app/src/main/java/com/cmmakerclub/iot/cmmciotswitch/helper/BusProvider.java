package com.cmmakerclub.iot.cmmciotswitch.helper;

public class BusProvider {
    private static final AppBus BUS = new AppBus();



    public static AppBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}