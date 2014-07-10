package com.gerenvip.endcalldemo;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.gerenvip.endcalldemo.telephony.DualPhoneMgr;

import java.lang.reflect.Method;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private InnerReceiver receiver;

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")
                    && intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.e(TAG, "外拨电话====" + mPhoneNumber);
            } else {// 来电
                Log.i(TAG, "is comming new call  ===============================");
                Log.i(TAG, "device model ===" + android.os.Build.MODEL);

                String mIncommingNumber = intent.getStringExtra("incoming_number");// 来电号码
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                Log.i(TAG, "the action =====" + intent.getAction());
                switch (tm.getCallState()) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.i(TAG, "call is idle ===" + mIncommingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        DualPhoneMgr manager = new DualPhoneMgr(context);
                        boolean endCall = manager.endCall();
                        Toast.makeText(context, "block incomming call is sucess === " + endCall, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "block incomming call is sucess === " + endCall);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.i(TAG, "call is offhook ===" + mIncommingNumber);
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.PHONE_STATE_2");
        filter.addAction("android.intent.action.PHONE_STATE2");
        filter.addAction("android.intent.action.DUAL_PHONE_STATE");
        filter.addAction("android.intent.action.PHONE_STATE_EXT");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);
        //test
//        EndCallExt ext = new EndCallExt();
//        ArrayList<String> serviceList = ext.getServiceList("service list");
//        Log.e(TAG, "service list :::"+serviceList);

        getAllMethod();
//        getTeleMgrMethod();
//        getSmsMessageMethod();

    }

    private void getAllMethod() {
        Class<?> clazz;
        try {
//           clazz = Class.forName("com.android.internal.telephony.ISms");
//            clazz = SmsManager.class;
            clazz = SmsMessage.class;
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                Log.e(TAG, "method:::" + m.getName());
                int length = m.getParameterTypes().length;
                int i = 0;
                while (i <= length - 1) {
                    Log.e(TAG, "[" + i + "]paramType == " + m.getParameterTypes()[i].getName());
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTeleMgrMethod() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Method[] methods = tm.getClass().getDeclaredMethods();
        for (Method m : methods) {
            Log.e(TAG, " tm method:::" + m.getName());
            int length = m.getParameterTypes().length;
            int i = 0;
            while (i <= length - 1) {
                Log.e(TAG, "[" + i + "]paramType == " + m.getParameterTypes()[i].getName());
                i++;
            }
        }

    }

    private void getSmsMessageMethod() {
        try {
            Class<?> clazz = Class.forName("com.android.internal.telephony.gsm.SmsMessage");
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                Log.e(TAG, " smsMessage method:::" + m.getName());
                int length = m.getParameterTypes().length;
                int i = 0;
                while (i <= length - 1) {
                    Log.e(TAG, "[" + i + "]paramType == " + m.getParameterTypes()[i].getName());
                    i++;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
