package com.zperkowski.peersharing;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkService extends IntentService {
    static final private String TAG = "NetworkService";
    public static final String ACTION_DOWNLOAD = "com.zperkowski.peersharing.action.DOWNLOAD";
    public static final String ACTION_REFRESH = "com.zperkowski.peersharing.action.REFRESH";
    public static final String ACTION_GETFILES = "com.zperkowski.peersharing.action.GETFILES";
    public static final String ACTION_STARTSERVER = "com.zperkowski.peersharing.action.STARTSERVER";
    public static final String ACTION_STOPSERVER = "com.zperkowski.peersharing.action.STOPSERVER";

    public static final String EXTRA_IP = "com.zperkowski.peersharing.extra.PARAM1";
    public static final String EXTRA_PATH = "com.zperkowski.peersharing.extra.PARAM2";

    public static final int NOTIFICATION_DOWNLOAD = 100;

    private Handler handler;

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_IP);
                final String param2 = intent.getStringExtra(EXTRA_PATH);
                downloadFile(param1, param2);
            } else if (ACTION_GETFILES.equals(action)) {
                final String ipParam = intent.getStringExtra(EXTRA_IP);
                getFiles(ipParam);
            } else if (ACTION_REFRESH.equals(action)) {
                refresh();
            } else if (ACTION_STARTSERVER.equals(action)) {
                startServer();
            } else if (ACTION_STOPSERVER.equals(action)) {
                stopServer();
            }
        }
    }

    private void stopServer() {
        Log.d(TAG, "stopServer()");
        Server.getServer().stopServer();
    }

    private void startServer() {
        Log.d(TAG, "startServer()");
        Server.getServer().startServer();
    }

    private void refresh() {
        Log.d(TAG, "refresh()");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.refreshed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFiles(String ip) {
        Log.d(TAG, "getFiles(" + ip + ")");
    }

    private void downloadFile(String ip, String path) {
        Log.d(TAG, "ip: " + ip + " path: " + path);

        Notification notificationDownloading = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.downloading))
                .build();
        Notification notificationDownloaded = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.downloaded))
                .build();

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_DOWNLOAD, notificationDownloading);
        synchronized (this) {
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nManager.notify(NOTIFICATION_DOWNLOAD, notificationDownloaded);
    }

}
