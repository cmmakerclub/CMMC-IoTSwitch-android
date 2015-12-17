package com.cmmakerclub.iot.cmmciotswitch.helper;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class AppBus extends Bus{
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void postQueue(final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(obj);
            }
        });
    }

}
