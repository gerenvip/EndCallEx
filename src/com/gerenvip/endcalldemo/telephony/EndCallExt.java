package com.gerenvip.endcalldemo.telephony;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android.internal.telephony.ITelephony;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

/**
 * 挂电话的增强类
 * 
 * @author wangwei
 */
public class EndCallExt {
    private static final String TAG = "EndCallExt";
    public static EndCallExt endCallExtInstance;
    private final ArrayList<EndCallInfo> allEndCallMethod = new ArrayList<EndCallInfo>();

    public EndCallExt() {
    }

    public EndCallExt(Context context) {
        ArrayList<String> serviceList = getServiceList("service list");
        ArrayList<ServiceInfo> serviceList2 = new ArrayList<ServiceInfo>();// 保存所有以phone开头的服务
        int j = serviceList.size();
        for (int k = 0; k < j; k++) {
            Log.d(TAG, "serviceList.get(" + k + ") =String ==" + serviceList.get(k));

            ServiceInfo serviceInfo = new ServiceInfo(serviceList.get(k));
            Log.d(TAG, "serviceList.get(" + k + ") =serviceInfo==" + serviceInfo);
            if (serviceInfo.getName() != null && serviceInfo.getName().startsWith("phone")) {
                serviceList2.add(serviceInfo);
                Log.e(TAG, "start with 'phone' ==" + serviceInfo.getName());
            }
        }
        int m = serviceList2.size();
        Log.e(TAG, "serviceList2 size ==" +m);
        int i = 0;
        while (i < m) {
            ArrayList<EndCallInfo> endCallInfoList = getAllEndCallMethod(serviceList2.get(i));
            allEndCallMethod.addAll(endCallInfoList);
            i++;
        }
    }

    /**
     * 获取EndCallExt的实例
     * 
     * @param context
     * @return
     */
    public static EndCallExt getInstance(Context context) {
        if (endCallExtInstance == null) {
            endCallExtInstance = new EndCallExt(context);
        }
        return endCallExtInstance;
    }

    /**
     * 拿到系统的所有service列表
     * 
     * @param serviceName 固定值 service list
     * @return 返回系统所有的service列表
     */
    public ArrayList<String> getServiceList(String serviceName) {

        ArrayList<String> localArrayList = new ArrayList<String>();
        try {
            InputStream is = Runtime.getRuntime().exec(serviceName).getInputStream();
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    if (is == null) {
                        break;
                    }
                    localBufferedReader.close();
                    is.close();
                    return localArrayList;
                }
                localArrayList.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localArrayList;
    }

    /**
     * 拿到多有以end开头的方法（可能是挂电话的方法）
     * 
     * @param serviceInfo
     * @return
     */
    public ArrayList<EndCallInfo> getAllEndCallMethod(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return new ArrayList<EndCallInfo>();
        }

        Object asInterfaceResult = getAsInterfaceResult(serviceInfo);
        Class<?> clazz = asInterfaceResult.getClass();

        ArrayList<EndCallInfo> endCallInfoList = new ArrayList<EndCallInfo>();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("end")) {// 以end开头的方法可能是挂电话的方法
                EndCallInfo endCallInfo = new EndCallInfo();
                endCallInfo.setServiceInfo(serviceInfo);
                endCallInfo.setObj(asInterfaceResult);
                endCallInfo.setMethodName(method.getName());
                endCallInfo.setParameterTypes(method.getParameterTypes());
                endCallInfoList.add(endCallInfo);
                Log.e(TAG, "endCall method ------------" + method.getName());
            }
        }
        return endCallInfoList;

    }

    /**
     * 拿到系统的asInterface方法执行后的返回值
     * 
     * @param serviceInfo 系统的一个service的信息
     * @return
     */
    public Object getAsInterfaceResult(ServiceInfo serviceInfo) {
        Object obj = null;
        try {
            Class<?> clazz = Class.forName(serviceInfo.getPackageName() + "$Stub");
            IBinder iBinder = checkService(serviceInfo.getName());
            obj = clazz.getMethod("asInterface", new Class[] {
                    IBinder.class
            }).invoke(null, new Object[] {
                    iBinder
            });
            if (obj != null) {
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ITelephony.Stub.asInterface(checkService("phone"));
    }

    /**
     * 反编译系统的checkService方法
     * 
     * @param serviceName 服务名称例如“phone”等
     * @return checkService 执行后的返回值
     */
    public IBinder checkService(String serviceName) {
        try {
            Method checkServiceMethod = Class.forName("android.os.ServiceManager").getMethod("checkService",
                    new Class[] {
                        String.class
                    });
            Object[] params = new Object[1];
            params[0] = new String(serviceName);
            IBinder iBinder = (IBinder) checkServiceMethod.invoke(null, params);
            return iBinder;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通用的反射方法
     * 
     * @param obj 执行反射的对象，可用来获取class，和执行invoke操作
     * @param methodName 方法名称
     * @param paramTypeClass 方法的参数类型
     * @param paramObj 方法所需参数
     * @return 返回方法执行的返回值结果
     */
    private Object commonReflection(Object obj, String methodName, Class<?>[] paramTypeClass,
            Object[] paramObj) {

        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, paramTypeClass);
            method.setAccessible(true);
            Object resultObj = method.invoke(obj, paramObj);
            return resultObj;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Boolean.valueOf(false);
    }

    /**
     * 挂电话
     * 
     * @return false 挂电话失败；true 挂电话成功；
     */
    public boolean endCall() {
        int i = 0;
        EndCallInfo info;
        boolean isOk = false;// 标记挂电话是否成功

        while (i < allEndCallMethod.size()) {
            info = allEndCallMethod.get(i);
            Object obj = info.getObj();
            if (obj == null) {
                obj = getAsInterfaceResult(info.getServiceInfo());
            }

            if (info.getParameterTypes() == null && info.getParameterTypes().length != 0) {
                // error 不需要参数挂电话的情况
                i++;
                continue;
            }
            if (info.getParameterTypes() == null || info.getParameterTypes().length == 0) {
                // 挂电话不需要参数
                isOk = ((Boolean) commonReflection(obj, info.getMethodName(), null, null)).booleanValue();
                if (isOk) {
                    // 挂电话成功，停止下面的操作 返回true；
                    break;
                }

            } else {// 挂电话需要参数
                int j = 0;
                boolean isOk2 = false;
                while (j < 10) {// 假设一个手机最多可以支持10张卡
                    String name = info.getMethodName();
                    Class<?>[] paramTypeClass = info.getParameterTypes();
                    Object[] paramObj = new Object[1];
                    paramObj[0] = Integer.valueOf(j);
                    isOk2 = ((Boolean) commonReflection(obj, name, paramTypeClass, paramObj)).booleanValue();
                    if (isOk2) {
                        isOk = isOk2;
                        Log.e(TAG, "the +" + name + "(" + j + ")is sucess===" + isOk);
                        break;
                    }
                    j++;
                }
                if (isOk) break;
            }
            i++;
        }
        Log.e(TAG, "isOk ======" + isOk);
        return isOk;
    }
}
