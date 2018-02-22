package com.zperkowski.peersharing;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

public class NetworkService extends IntentService {
    static final private String TAG = "NetworkService";
    public static final String ACTION_DOWNLOAD = "com.zperkowski.peersharing.action.DOWNLOAD";
    public static final String ACTION_UPLOADING = "com.zperkowski.peersharing.action.UPLOADING";
    public static final String ACTION_REFRESH = "com.zperkowski.peersharing.action.REFRESH";
    public static final String ACTION_GETFILES = "com.zperkowski.peersharing.action.GETFILES";
    public static final String ACTION_LISTOFFILES = "com.zperkowski.peersharing.action.LISTOFFILES";
    public static final String ACTION_STARTSERVER = "com.zperkowski.peersharing.action.STARTSERVER";
    public static final String ACTION_STOPSERVER = "com.zperkowski.peersharing.action.STOPSERVER";
    public static final String MAGIC_CHAR = "~~";

    public static final String EXTRA_IP = "com.zperkowski.peersharing.extra.EXTRA_IP";
    public static final String EXTRA_PATH = "com.zperkowski.peersharing.extra.EXTRA_PATH";

    public static final int NOTIFICATION_DOWNLOAD = 100;

    private Handler handler;
    private ClientUDP clientUDP;

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
                final String manualAddress = intent.getStringExtra(EXTRA_IP);
                getFiles(manualAddress);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUDP.getServer().stopServer();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerTCP.getServer().stopServer();
            }
        }).start();
        if (clientUDP != null)
            clientUDP.cancel(true);
        Log.d(TAG, "Servers stopped");
    }

    private void startServer() {
        Log.d(TAG, "startServer()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerUDP.getServer().startServer();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerTCP.getServer().startServer();
            }
        }).start();
        Log.d(TAG, "Servers started");
    }

    private void refresh() {
        Log.d(TAG, "refresh()");
        clientUDP = new ClientUDP();
        clientUDP.execute();
    }

    private void getFiles(String manualAddress) {
        Log.d(TAG, "getFiles(" + manualAddress + ")");
        ClientTCP clientTCP = new ClientTCP();
        clientTCP.execute(manualAddress, ACTION_GETFILES);
        // TODO: Add 2nd parameter to execute above to send path. If not set should send just / (root directory)

    }

    private void downloadFile(String ip, String path) {
        Log.d(TAG, "IP: " + ip + " path: " + path);

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
        ClientTCP clientTCP = new ClientTCP();
        clientTCP.execute(ip, NetworkService.ACTION_DOWNLOAD, String.valueOf(FileUtils.findFile(path).getSize()), path);
        nManager.notify(NOTIFICATION_DOWNLOAD, notificationDownloaded);
    }

}
