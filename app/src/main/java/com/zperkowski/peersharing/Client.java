package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, String> {

    private String dstAddress;
    private int dstPort;
    private String response = "";
    final static private String TAG = "Client";
    private Socket socket = null;

    Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
    }

    @Override
    protected void onCancelled() {
        try {
            Log.d(TAG, "Closing Client socket");
            if (socket != null)
                socket.close();
            Log.d(TAG, "Closed Client socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            Log.d(TAG, "Running...");

            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] sendData = TAG.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.1.0"), Server.getPort());
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + InetAddress.getByName("192.168.1.0").getHostAddress());

			/*
             * notice: inputStream.read() will block if no data return
			 */
            Log.d(TAG, "Listening...");
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                Log.d(TAG, "Reading...");
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//            }
            Log.d(TAG, "Read");

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, result);
        super.onPostExecute(result);
    }


//    InetAddress getBroadcastAddress() throws IOException {
//        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
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