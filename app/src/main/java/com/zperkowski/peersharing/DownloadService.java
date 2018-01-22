package com.zperkowski.peersharing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {
    static final private String TAG = "DownloadService";
    public static final String ACTION_DOWNLOAD = "com.zperkowski.peersharing.action.DOWNLOAD";

    public static final String EXTRA_IP = "com.zperkowski.peersharing.extra.PARAM1";
    public static final String EXTRA_PATH = "com.zperkowski.peersharing.extra.PARAM2";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_IP);
                final String param2 = intent.getStringExtra(EXTRA_PATH);
                downloadFile(param1, param2);
            }
        }
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
