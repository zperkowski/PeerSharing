package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
        if (arg0.length == 1)
            return sendUnicast(arg0[0], ServerTCP.getPort());
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, result);
        super.onPostExecute(result);
    }
    private String sendUnicast(String address, int port) {
        try {
            Log.d(TAG, "sendUnicast(" + address + "," + port +")");
            socket = new Socket(address, port);

            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print("");
            printStream.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}