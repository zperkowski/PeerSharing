package com.zperkowski.peersharing;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerUDP {
    private static final String TAG = "ServerUDP";
    private static ServerUDP sServerUDP;
    private ServerThread serverThread;
    private DatagramSocket serverSocket;
    private String data, connectionIP;
    private final static int PORT = 6666;
    byte[] recvBuf = new byte[15000];

    public static ServerUDP getServer() {
        Log.d(TAG, "getServer()");
        if (sServerUDP == null) {
            sServerUDP = new ServerUDP();
        }
        return sServerUDP;
    }

    public static int getPort() {
        return PORT;
    }

    void startServer() {
        Log.d(TAG, "startServer()");
        if (serverThread == null)
            serverThread = new ServerThread();
        serverThread.run();
    }

    void stopServer() {
        Log.d(TAG, "stopServer()");
        if (serverThread != null)
            serverThread.interrupt();
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while (true)
                try {
                    serverSocket = new DatagramSocket(PORT, InetAddress.getByName(NetworkUtils.getNetworkAddress()));
                    serverSocket.setBroadcast(true);
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    Log.d(TAG, "Waiting for UDP broadcast: " + InetAddress.getByName(NetworkUtils.getNetworkAddress()) + " " + PORT);
                    serverSocket.receive(packet);
                    data = new String(packet.getData()).trim();
                    connectionIP = packet.getAddress().toString().substring(1);
                    // If connection address is diffrent than its own
                    if (!connectionIP.equals(NetworkUtils.getIPAddress())) {
                        Log.d(TAG, "Connection from " + connectionIP + ":" + serverSocket.getPort() + "\n");
                        MainActivity.addPhoneToList(new Phone(connectionIP));
                        // Creates new TCP client to answer
                        ClientTCP clientTCP = new ClientTCP();
                        clientTCP.execute(connectionIP);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "ServerThread.run() error");
                    e.printStackTrace();
                } finally {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                }
        }
    }
}
