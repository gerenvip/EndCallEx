package com.gerenvip.endcalldemo.telephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

public class DualPhoneMgr {
    private static final String TAG = "DualPhoneMgr";
    private Context context;
    Class<?> clazz = null;
    Method getITelephonyMethod = null;
    protected ITelephony iTelephony;

    public DualPhoneMgr(Context context) {
        this.context = context;
        iTelephony = getTelephonyService();
    }

    public boolean isHasITelephony() {
        if (this.iTelephony == null) {
            this.iTelephony = getTelephonyService();
        }
        return this.iTelephony != null;
    }

    public boolean endCall() {
//        silenceRinger();
        boolean isHasITelephony = isHasITelephony();
        Log.e(TAG, "is has ITelephony =====" + isHasITelephony);
        boolean isOk = false;// 标记是否挂断成功
        try {
            isOk = iTelephony.endCall();
            Log.e(TAG, "first block call is sucess? ==" + isOk);
            if (isOk) {
                return true;
            } else {// 挂电话失败
                EndCallExt instance = EndCallExt.getInstance(context);
                //note 有些手机挂电话失败也返回true，这个时候需要对该手机单独做适配，适配方式：反射找到挂电话的真正方法，然后针对该手机invoke该方法
                isOk = instance.endCall();
                Log.e(TAG, "second block call is sucess? ==" + isOk);
            }
            return isOk;
        } catch (Exception e) {
            try {
                return getTelephonyService().endCall();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        return isOk;
    }

    public ITelephony getTelephonyService() {
        try {
            clazz = Class.forName("com.android.internal.telephony.ITelephony", false, Thread.currentThread()
                    .getContextClassLoader());
            getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony",
                    new Class[0]);
            getITelephonyMethod.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (getITelephonyMethod != null) {
            try {
                TelephonyManager obj = (TelephonyManager) context.getSystemService("phone");
                Method localMethod = getITelephonyMethod;
                Object[] arrayOfObject = new Object[0];
                return (ITelephony) localMethod.invoke(obj, arrayOfObject);
            } catch (IllegalAccessException localIllegalAccessException) {
            } catch (InvocationTargetException localInvocationTargetException) {
            }
        }
        return null;
    }


    public void silenceRinger() {
        isHasITelephony();
        try {
            iTelephony.silenceRinger();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
