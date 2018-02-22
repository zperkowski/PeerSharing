package com.zperkowski.peersharing;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                        Log.d(TAG, "Reading... bytesRead: " + bytesRead);
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        message += byteArrayOutputStream.toString("UTF-8");
                        Log.d(TAG, "Part of message: " + message);
                    }
                    Log.d(TAG, "Full message: " + message);
                    // No information - add IP to the list
                    if (message.length() == 0) {
                        if (!socket.getInetAddress().equals(NetworkUtils.getIPAddress()))
                            MainActivity.addPhoneToList(new Phone(socket.getInetAddress()));
                    } else {
                        String firstPartOfMessage = message.split(NetworkService.MAGIC_CHAR)[0];
                        Log.d(TAG, "firstPartOfMessage: " + firstPartOfMessage);
                        switch (firstPartOfMessage) {
                            case NetworkService.ACTION_GETFILES:
                                String files = FileUtils.getFilesList();
                                SocketServerReplyThread replyThread = new SocketServerReplyThread(socket.getInetAddress().toString().substring(1), ServerTCP.getPort(), files);
                                replyThread.run();
                                break;
                            case NetworkService.ACTION_LISTOFFILES:
                                // TODO: Get message and read list
                                break;
                        }
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

        private static final String TAG = "ServerTCP";
        private Socket hostThreadSocket;
        private String files;
        private int port;
        private String address;

        SocketServerReplyThread(String address, int port, String files) {
            Log.d(TAG, "SocketServerReplyThread(" + address + ", " + port + ", " + files + ")");
            this.address = address;
            this.port = port;
            this.files = files;
        }

        @Override
        public void run() {
            String message = NetworkService.ACTION_LISTOFFILES + NetworkService.MAGIC_CHAR + files;
            Log.d(TAG, "SocketServerReplyThread.run() with message: " + message);
            OutputStream outputStream;
            try {
                hostThreadSocket = new Socket(address, port);
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(message);
                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "SocketServerReplyThread(" + hostThreadSocket.getInetAddress().toString() + ", " + port + ", " + message + ")");
            }
        }

    }

}