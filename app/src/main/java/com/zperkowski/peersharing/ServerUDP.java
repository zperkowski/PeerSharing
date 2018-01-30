package com.zperkowski.peersharing;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerUDP {
    private static final String TAG = "ServerUDP";
    private static ServerUDP sServerUDP;
    private ServerThread serverThread;
    private DatagramSocket serverSocket;
    private String message;
    private String connectionIP;
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
                    message = "";
                    serverSocket = new DatagramSocket(PORT, InetAddress.getByName(NetworkUtils.getNetworkAddress()));
                    serverSocket.setBroadcast(true);
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    Log.d(TAG, "Waiting for UDP broadcast");
                    serverSocket.receive(packet);
                    connectionIP = new String(packet.getData()).trim();
                    if (!connectionIP.equals(NetworkUtils.getIPAddress())) {
                        message += "Connection from " + connectionIP + "\n";
                        Log.d(TAG, message);
                        MainActivity.addPhoneToList(new Phone(connectionIP));
//
//                        SocketServerReplyThread replyThread = new SocketServerReplyThread(connectionIP);
//                        replyThread.run();
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

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from ServerUDP, you are connected";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";
                Log.d(TAG, message);

            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
            Log.d(TAG, message);
        }

    }

}