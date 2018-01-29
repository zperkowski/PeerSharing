package com.zperkowski.peersharing;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error");
        }
        return "";
    }

    public static String getIpAddress(byte[] rawBytes) {
        int i = 4;
        String ipAddress = "";
        for (byte raw : rawBytes)
        {
            ipAddress += (raw & 0xFF);
            if (--i > 0)
            {
                ipAddress += ".";
            }
        }

        return ipAddress;
    }

//    public static InetAddress getBroadcastAddress() throws IOException {
//        WifiManager wifi = (WifiManager) Context.getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo dhcp = wifi.getDhcpInfo();
//        // handle null somehow
//
//        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
//        byte[] quads = new byte[4];
//        for (int k = 0; k < 4; k++)
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//        return InetAddress.getByAddress(quads);
//    }
}
