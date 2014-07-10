package com.gerenvip.endcalldemo.telephony;


/**
 * 记录所有可能的endCall方法的一些信息
 * @author wangwei
 *
 */
public class EndCallInfo {

    /**
     * 执行该方法的对象
     */
    private Object obj;
    private String methodName;
    private Class<?>[] parameterTypes;
    private ServiceInfo serviceInfo;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

}
