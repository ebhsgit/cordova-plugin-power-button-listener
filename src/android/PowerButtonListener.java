package com.eightbhs;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

public class PowerButtonListener extends CordovaPlugin {

    private static final String LOG_TAG = "PowerButtonLog";
    private CallbackContext powerButtonCallbackContext;

    private BroadcastReceiver broadcastReceiver;

    public PowerButtonListener() {
        powerButtonCallbackContext = null;
        broadcastReceiver = null;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("start")) {

            // Check if the plugin is listening the power button events
            if (this.powerButtonCallbackContext != null) {
                // Send error to new context
                callbackContext.error("Power button listener already running");
                return true;
            }
            // Get the reference to the callbacks and start the listening process
            this.powerButtonCallbackContext = callbackContext;

            if (this.broadcastReceiver == null) {
                this.attachReceiver();
            }

            // Don't return any result now. Status is sent when power button event is
            // intercepted by broadcast receiver
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.powerButtonCallbackContext.sendPluginResult(pluginResult);
            return true;
        } else if (action.equals("stop")) {
            this.removeBroadcastReceiver();

            // Erase the callbacks reference and stop the listening process
            this.sendButtonEvent(new JSONObject(), false); // release status callback in Javascript side
            this.powerButtonCallbackContext = null;
            callbackContext.success();
            return true;
        }

        return false;
    }

    private void attachReceiver() {
        // Use Screen on and off to infer power button.
        // This is also triggered when screen time out.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();

                    JSONObject info = new JSONObject();
                    info.put("platform", new String("android"));
                    info.put("keyAction", action);

                    if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_SCREEN_ON)) {
                        info.put("keyCode", KeyEvent.KEYCODE_POWER);
                    } else {
                        // Shouldn't get here given filter action
                        // Pass back to caller to allow understanding of what happened.
                        info.put("keyCode", "unknown");
                        Log.e(LOG_TAG, "Unexpected action for BroadcastReceiver: " + action);
                    }

                    sendButtonEvent(info, true);
                } 
                catch (JSONException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        };

        this.webView.getContext().registerReceiver(this.broadcastReceiver, intentFilter);
    }

    public void onDestroy() {
        this.removeBroadcastReceiver();
    }

    public void onReset() {
        this.removeBroadcastReceiver();
    }

    private void removeBroadcastReceiver() {
        if (this.broadcastReceiver != null) {
            try {
                this.webView.getContext().unregisterReceiver(this.broadcastReceiver);
                this.broadcastReceiver = null;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error unregistering broadcast receiver: " + e.getMessage(), e);
            }
        }
    }

    private void sendButtonEvent(JSONObject info, boolean keepCallback) {
        if (this.powerButtonCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(keepCallback);
            this.powerButtonCallbackContext.sendPluginResult(result);
        }
    }
}