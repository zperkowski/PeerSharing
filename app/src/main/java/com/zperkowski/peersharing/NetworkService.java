package com.zperkowski.peersharing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NetworkService extends IntentService {
    static final private String TAG = "NetworkService";
    public static final String ACTION_DOWNLOAD = "com.zperkowski.peersharing.action.DOWNLOAD";
    public static final String ACTION_REFRESH = "com.zperkowski.peersharing.action.REFRESH";
    public static final String ACTION_GETFILES = "com.zperkowski.peersharing.action.GETFILES";

    public static final String EXTRA_IP = "com.zperkowski.peersharing.extra.PARAM1";
    public static final String EXTRA_PATH = "com.zperkowski.peersharing.extra.PARAM2";

    public NetworkService() {
        super("NetworkService");
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
            }
        }
    }

    private void refresh() {
        Log.d(TAG, "refresh()");
    }

    private void getFiles(String ip) {
        Log.d(TAG, "getFiles(" + ip + ")");
    }

    private void downloadFile(String ip, String path) {
        synchronized (this) {
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "ip: " + ip + " path: " + path);
    }

}
