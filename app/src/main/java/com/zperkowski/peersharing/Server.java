package com.zperkowski.peersharing;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final String TAG = "Server";
    private static Server sServer;
    private ServerThread serverThread;
    private ServerSocket serverSocket;
    private String message;
    private final static int PORT = 5555;

    public static Server getServer() {
        Log.d(TAG, "getServer()");
        if (sServer == null) {
            sServer = new Server();
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
            try {
                serverSocket = new ServerSocket(PORT);
                Socket socket = serverSocket.accept();
                message += " from "
                        + socket.getInetAddress() + ":"
                        + socket.getPort() + "\n";
                Log.d(TAG, message);
                SocketServerReplyThread replyThread = new SocketServerReplyThread(socket);
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
