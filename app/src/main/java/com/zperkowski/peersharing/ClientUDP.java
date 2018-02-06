package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientUDP extends AsyncTask<Void, Void, String> {

    private String response = "";
    final static private String TAG = "ClientUDP";
    private Socket socket = null;


    @Override
    protected void onCancelled() {
        try {
            Log.d(TAG, "Closing ClientUDP socket");
            if (socket != null)
                socket.close();
            Log.d(TAG, "Closed ClientUDP socket");
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
            Log.d(TAG, "Sending UDP: " + NetworkUtils.getIPAddress() + ":" + socket.getPort());
            byte[] sendData = NetworkUtils.getIPAddress().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(NetworkUtils.getNetworkAddress()), ServerUDP.getPort());
            socket.send(sendPacket);
            Log.d(TAG, getClass().getName() + "Broadcast packet sent to: " + InetAddress.getByName(NetworkUtils.getNetworkAddress()) + ":" + socket.getPort());
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
}