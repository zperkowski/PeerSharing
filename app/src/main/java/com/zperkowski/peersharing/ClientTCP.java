package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientTCP extends AsyncTask<String, Void, String> {
    private String response = "";
    final static private String TAG = "ClientTCP";
    private Socket socket = null;

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
    protected String doInBackground(String... arg0) {
        String address = arg0[0];
        if (arg0.length == 1)
            sendUnicast(address, ServerTCP.getPort());
        else if (arg0.length == 2)
            switch (arg0[1]){
                case NetworkService.ACTION_GETFILES:
                    getFiles(address, ServerTCP.getPort());
                    break;
            }
        return ""; //TODO: Return something useful
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, result);
        super.onPostExecute(result);
    }

    private List getFiles(String address, int port) {
        Log.d(TAG, "getFiles(" + address + ", " + port + ")");
        // Prepare
        String message = "";
        int bytesRead;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        List files = new ArrayList();
        // Send request to get list of files from server
        send(address, port, NetworkService.ACTION_GETFILES);
        // Read list of files
//        try {
//            InputStream inputStream = socket.getInputStream();
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                Log.d(TAG, "getFiles - Reading");
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                message += byteArrayOutputStream.toString("UTF-8");
//            }
//            socket.close();
//            Log.d(TAG, "List of files: " + files);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return files;
    }

    private void sendUnicast(String address, int port) {
        Log.d(TAG, "sendUnicast(" + address + "," + port +")");
        send(address, port, "");
    }

    private void send(String address, int port, String message) {
        Log.d(TAG, "send(" + address + ", " + port + ", " + message + ")");
        try {
            socket = new Socket(address, port);

            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(message);
            printStream.flush();
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
