package com.zperkowski.peersharing;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {
    private static final String TAG = "ServerTCP";
    private static ServerTCP sServer;
    private ServerThread serverThread;
    private ServerSocket serverSocket;
    private String message;
    private final static int PORT = 6666;

    public static ServerTCP getServer() {
        Log.d(TAG, "getServer()");
        if (sServer == null) {
            sServer = new ServerTCP();
        }
        return sServer;
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
                    serverSocket = new ServerSocket(PORT);
                    Socket socket = serverSocket.accept();
                    Log.d(TAG, "Connection from "
                            + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n");
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        Log.d(TAG, "Reading...");
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        message += byteArrayOutputStream.toString("UTF-8");
                    }
                    // No information - add IP to the list
                    if (message.length() == 0) {
                        if (!socket.getInetAddress().equals(NetworkUtils.getIPAddress()))
                            MainActivity.addPhoneToList(new Phone(socket.getInetAddress()));
                    }
                } catch (IOException e) {
                    Log.e(TAG, "ServerThread.run() error");
                    e.printStackTrace();
                } finally {
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "ServerThread try finally error");
                            e.printStackTrace();
                        }
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
            String msgReply = "Hello from Server, you are connected";

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