package com.zperkowski.peersharing;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private final static int UPLOAD_PORT = 6667;

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

    public static int getUploadPort() {
        return UPLOAD_PORT;
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
                        message = byteArrayOutputStream.toString("UTF-8");
                        Log.d(TAG, "Part of message:\t" + message);
                    }
                    Log.d(TAG, "Full message:\t" + message);
                    Log.d(TAG, "Len message: " + message.length());
                    // No information - add IP to the list
                    if (message.length() == 0) {
                        if (!socket.getInetAddress().equals(NetworkUtils.getIPAddress()))
                            MainActivity.addPhoneToList(new Phone(socket.getInetAddress()));
                    } else {
                        JSONArray json;
                        try {
                            json = new JSONArray(message);
                            String firstPartOfMessage = json.getString(0);
                            Log.d(TAG, "firstPartOfMessage: " + firstPartOfMessage);
                            String socketAddress = socket.getInetAddress().toString().substring(1);
                            switch (firstPartOfMessage) {
                                case NetworkService.ACTION_GETFILES:
    //                                String files = FileUtils.getStringOfFiles();
                                    String files = FileUtils.getJSONOfFiles().toString();
                                    FilesReplyThread replyThread = new FilesReplyThread(socketAddress, ServerTCP.getPort(), files);
                                    replyThread.run();
                                    break;
                                case NetworkService.ACTION_LISTOFFILES:
                                    FilesActivity.setFilesList(FileUtils.getListOfFiles(message));
                                    break;
                                case NetworkService.ACTION_DOWNLOAD:
                                    UploadReplyThread uploadReplyThread = new UploadReplyThread(socketAddress, ServerTCP.getUploadPort(), message);
                                    uploadReplyThread.run();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "run: Parsing JSON failed");
                            e.printStackTrace();
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

    private class UploadReplyThread extends Thread {
        private static final String TAG = "STCP.UploadReplyThread";
        private ServerSocket threadSocket;
        private String path;
        private String address;
        private int port;

        UploadReplyThread(String address, int port, String path) {
            Log.d(TAG, "UploadReplyThread(" + address + ", " + port + ", " + path + ")");
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(path);
                this.path = jsonArray.get(1).toString();
                this.address = address;
                this.port = port;
            } catch (JSONException e) {
                Log.e(TAG, "UploadReplyThread: Parsing JSON fiailed");
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
                try {
                    threadSocket = new ServerSocket(port);
                    Socket socket = threadSocket.accept();
                    Log.d(TAG, "UploadReplyThread accepted");
                    // Wait for the path
                    InputStream istream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
                    path = bufferedReader.readLine();
                    // Send the file
                    Log.d(TAG, "UploadReplyThread path: " + path);
                    File file = new File(path);
                    Log.d(TAG, file.getPath() + " " + file.canRead());
                    byte[] bytes = new byte[(int) file.length()];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(bytes, 0, bytes.length);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.flush();
                    outputStream.close();
                    Log.d(TAG, "UploadReplyThread file sent");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    private class FilesReplyThread extends Thread {
        private static final String TAG = "STCP.FilesReplyThread";
        private Socket threadSocket;
        private String files;
        private int port;
        private String address;

        FilesReplyThread(String address, int port, String files) {
            Log.d(TAG, "FilesReplyThread(" + address + ", " + port + ", " + files + ")");
            Log.d(TAG, "FilesReplyThread files len: " + files.length());
            this.address = address;
            this.port = port;
            this.files = files;
        }

        @Override
        public void run() {
//            String message = NetworkService.ACTION_LISTOFFILES + NetworkService.MAGIC_CHAR + files;
            Log.d(TAG, "FilesReplyThread.run() with message: " + files);
            Log.d(TAG, "FilesReplyThread files len: " + files.length());
            OutputStream outputStream;
            try {
                threadSocket = new Socket(address, port);
                outputStream = threadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(files);
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "FilesReplyThread(" + threadSocket.getInetAddress().toString() + ", " + port + ", " + files + ")");
            }
        }

    }

}