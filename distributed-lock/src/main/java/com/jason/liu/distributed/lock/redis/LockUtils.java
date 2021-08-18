package com.jason.liu.distributed.lock.redis;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-01 15:26:45
 * @todo
 */
public class LockUtils {

    private static String LOCAL_MAC = localMAC();

    private static String JVM_ID = jvmPid();

    /**
     * 获取LockId
     * Mac+JvmID+threadId
     *
     * @return
     */
    public static String getLockId() {
        return LOCAL_MAC + "@" + JVM_ID + "@" + Thread.currentThread().getId();
    }

    /**
     * 获取本机网卡地址
     *
     * @return macAddress
     */
    public static String localMAC() {
        String localMac;
        try {
            InetAddress ia = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            localMac = sb.toString();
        } catch (Exception e) {
            //TODO 如果出现异常则采用一个默认的随机UUID作为Mac地址
            localMac = UUID.randomUUID().toString();
        }
        return localMac.toUpperCase().replace("-", "");
    }

    /**
     * 获取jvmPId
     *
     * @return jvmPid
     */
    public static String jvmPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0) {
            pid = pid.substring(0, indexOf);
            return pid;
        }
        return UUID.randomUUID().toString();
    }
}
