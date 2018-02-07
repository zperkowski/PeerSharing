package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
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
        List files = new ArrayList();
        Socket socket = send(address, port, NetworkService.ACTION_GETFILES);
        //TODO: Wait for a respond with list of files
        return files;
    }

    private void sendUnicast(String address, int port) {
        send(address, port, "");
    }

    private Socket send(String address, int port, String message) {
        try {
            Log.d(TAG, "sendUnicast(" + address + "," + port +")");
            socket = new Socket(address, port);

            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(message);
            printStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }
}