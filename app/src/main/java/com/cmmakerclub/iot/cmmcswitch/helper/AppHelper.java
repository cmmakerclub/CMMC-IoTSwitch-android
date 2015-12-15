package com.cmmakerclub.iot.cmmcswitch.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CompoundButton;

import butterknife.ButterKnife;

/**
 * Created by nat on 12/1/15 AD.
 */
public class AppHelper {
    public static final String TAG = AppHelper.class.getSimpleName();
    public static final ButterKnife.Setter<CompoundButton, Boolean> SET_COMPOUND_VIEW_CHECK = new ButterKnife.Setter<CompoundButton, Boolean>() {
        @Override
        public void set(CompoundButton view, Boolean value, int index) {
            Log.d(TAG, " (line 16): " + index);
            view.setChecked(value);
        }
    };


    public static final ButterKnife.Setter<CompoundButton, Integer> UPDATE_TOGGLE_STATE = new ButterKnife.Setter<CompoundButton, Integer>() {
        @Override
        public void set(CompoundButton view, Integer value, int index) {
            Log.d(TAG, "UPDATE_TOGGLE_STATE (line 27): " + index +" = " + view.isChecked());
        }
    };


    public static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        return mSharedPref;
    }

}
