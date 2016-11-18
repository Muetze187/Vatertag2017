package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Muetze187 on 08.11.2016.
 */

public class Blauzahn {

    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = "Blauzahn";

    private static Context context;

    private BluetoothAdapter adapter = null;
    private BluetoothSocket socket = null;
    private OutputStream stream_out = null;
    private InputStream stream_in = null;
    private boolean is_connected = false;
    private String mac_adresse;

    public Blauzahn(Context context) {
        this.context = context;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            // Toast.makeText(this, "Bitte Bluetooth aktivieren",
            //      Toast.LENGTH_LONG).show();
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(turnOn, 0);
            // Log.d(LOG_TAG,
            //       "onCreate: Bluetooth Fehler: Deaktiviert oder nicht vorhanden");
            //finish();
            // return;
        } else
            Log.d(LOG_TAG, "onCreate: Bluetooth-Adapter ist bereit");
    }


    public String getUUID() {
        return uuid.toString();
    }

    public void checkBT() {
        if (adapter == null || !adapter.isEnabled()) {
            Toast.makeText(context, "Bitte Bluetooth aktivieren",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Bluetooth aktiviert",
                    Toast.LENGTH_LONG).show();
        }

    }

    public static void verbinden(){

    }
}
