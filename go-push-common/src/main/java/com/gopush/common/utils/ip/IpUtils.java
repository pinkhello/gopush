package com.gopush.common.utils.ip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * go-push
 *
 * @类功能说明：Ip工具类
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/24 上午11:23
 * @VERSION：
 */
public class IpUtils {

    /***
     * 获取外网IP
     * @return
     */
    public static String internetIp() {
        try {

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress inetAddress = null;
            Enumeration<InetAddress> inetAddresses = null;
            while (networks.hasMoreElements()) {
                inetAddresses = networks.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null
                            && inetAddress instanceof Inet4Address
                            && !inetAddress.isSiteLocalAddress()
                            && !inetAddress.isLoopbackAddress()
                            && inetAddress.getHostAddress().indexOf(":") == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

            return null;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * 获取内网IP
     *
     * @return
     */
    public static String intranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
