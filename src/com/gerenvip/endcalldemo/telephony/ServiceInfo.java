package com.gerenvip.endcalldemo.telephony;

import android.text.TextUtils;
/**
 * 获取到的系统的Services
 * @author wangwei
 *
 */
public class ServiceInfo {

    private int id;
    private String name;
    private String packageName;

    public ServiceInfo(String serviceInfo) {
        String[] splitInfos = serviceInfo.split("\t");
        if ((splitInfos != null) && (splitInfos.length >= 1) && TextUtils.isDigitsOnly(splitInfos[0])) {
            this.id = Integer.parseInt(splitInfos[0]);// 记录id
            String[] splitInfos2 = splitInfos[1].split(":");
            this.name = splitInfos2[0];
            this.packageName = splitInfos2[1].replace("[", "").replace("]", "").trim();
        }
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

}
