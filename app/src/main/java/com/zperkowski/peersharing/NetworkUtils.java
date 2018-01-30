package com.zperkowski.peersharing;

import android.util.Log;
import android.util.Pair;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static String getNetworkAddress() {
        List<Pair<String, String>> networkInfo = getNetworkInfo(true);
        for (int i = 0; i < networkInfo.size(); i++) {
            if (networkInfo.get(i).first.equals("Network"))
                return networkInfo.get(i).second;
        }
        return "";
    }

    public static String getIPAddress() {
        List<Pair<String, String>> networkInfo = getNetworkInfo(true);
        for (int i = 0; i < networkInfo.size(); i++) {
            if (networkInfo.get(i).first.equals("Address"))
                return networkInfo.get(i).second;
        }
        return "";
    }

    public static String getBroadcastAddress() {
        List<Pair<String, String>> networkInfo = getNetworkInfo(true);
        for (int i = 0; i < networkInfo.size(); i++) {
            if (networkInfo.get(i).first.equals("Broadcast"))
                return networkInfo.get(i).second;
        }
        return "";
    }

    public static List<Pair<String, String>> getNetworkInfo(boolean useIPv4) {
        List<Pair<String, String>> addresses = new ArrayList<>();
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InterfaceAddress> addrs = intf.getInterfaceAddresses();
                for (InterfaceAddress addr : addrs) {
                    if (!addr.getAddress().isLoopbackAddress()) {
                        String sAddr = addr.getAddress().toString();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4) {
                                sAddr = sAddr.split("/")[1];
                                addresses.add(Pair.create("Network", getNetworkAddress(sAddr, addr.getNetworkPrefixLength())));
                                addresses.add(Pair.create("Address", sAddr));
                                addresses.add(Pair.create("Broadcast", addr.getBroadcast().toString()));
                                return addresses;
                            }
                        } else {
                            int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                            addresses.add(Pair.create("Network", ""));
                            addresses.add(Pair.create("Address", delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase()));
                            addresses.add(Pair.create("Broadcast", addr.getBroadcast().toString()));
                            return addresses;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error");
        }
        return addresses;
    }

    public static String getNetworkAddress(String ip, short mask) {
        int value = 0xffffffff << (32 - mask);
        byte[] maskAddr = new byte[]{
                (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };

        InetAddress netAddr = null;
        try {
            byte[] ipAddr = Inet4Address.getByName(ip).getAddress();
            for (int i = 0; i < ipAddr.length; i++) {
                ipAddr[i] = (byte) (ipAddr[i] & maskAddr[i]);
            }
            netAddr = InetAddress.getByAddress(ipAddr);
            return netAddr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIPAddress(byte[] rawBytes) {
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
}
