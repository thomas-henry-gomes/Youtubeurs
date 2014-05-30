package com.youtubeurs.lite2.service.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by thomas henry-gomes on 23/03/2014.
 * Class to auto launch the service at system start
 */
public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,AutoRefresh.class);
        context.startService(i);
    }
}
