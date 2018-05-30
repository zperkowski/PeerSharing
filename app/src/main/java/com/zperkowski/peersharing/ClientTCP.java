package com.zperkowski.peersharing;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

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
        else if (arg0.length > 1)
            switch (arg0[1]){
                case NetworkService.ACTION_GETFILES:
                    getFiles(address, ServerTCP.getPort());
                    return NetworkService.ACTION_GETFILES;
                case NetworkService.ACTION_DOWNLOAD:
                    int size = Integer.parseInt(arg0[2]);
                    String path = arg0[3];
                    downloadFile(address, ServerTCP.getPort(), size, path);
                    return NetworkService.ACTION_DOWNLOAD;
            }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
        if (result.equals(NetworkService.ACTION_DOWNLOAD))
            NetworkService.setNotificationDownloaded();
        super.onPostExecute(result);
    }

    private void downloadFile(String address, int port, int sizeOfFile, String path) {
        Log.d(TAG, "downloadFile: Sending download request: " + address + " " + port + " " + path);
        JSONArray json = new JSONArray();
        json.put(NetworkService.ACTION_DOWNLOAD);
        json.put(path);
        Log.d(TAG, "downloadFile size: " + sizeOfFile);
        String pathToSave = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String[] splitPath = path.split("/");
        pathToSave = pathToSave + "/" + splitPath[splitPath.length-1];

        send(address, port, json.toString());

        byte [] bytesOfFile  = new byte[sizeOfFile];
        int bytesRead, bytesCurrent;
        try {
            // Turning on server on 2nd device is slower
            Thread.sleep(250);
            Socket socket = new Socket(address, ServerTCP.getUploadPort());
            // Sending the path
            OutputStream ostream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(ostream, true);
            printWriter.println(path);
            printWriter.flush();
            // Waiting for file
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(pathToSave);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            Log.d(TAG, "downloadFile: Reading");
            bytesRead = inputStream.read(bytesOfFile);
            bytesCurrent = bytesRead;
            do {
                Log.d(TAG, "Read " + bytesRead + " bytes");
                bytesRead = inputStream.read(bytesOfFile, bytesCurrent, (bytesOfFile.length-bytesCurrent));
                if(bytesRead >= 0) bytesCurrent += bytesRead;
            } while(bytesRead > 0);
            bufferedOutputStream.write(bytesOfFile, 0 , bytesCurrent);
            bufferedOutputStream.flush();
            Log.d(TAG, "downloadFile: Read" + pathToSave);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getFiles(String address, int port) {
        Log.d(TAG, "getFiles(" + address + ", " + port + ")");
        // Send request to get list of files from server
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(NetworkService.ACTION_GETFILES);
        send(address, port, jsonArray.toString());
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
