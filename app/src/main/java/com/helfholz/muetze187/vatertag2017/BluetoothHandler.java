package com.helfholz.muetze187.vatertag2017;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import static com.helfholz.muetze187.vatertag2017.BlauzahnActivity.btArrayAdapter;
import static com.helfholz.muetze187.vatertag2017.BlauzahnActivity.filterBlauZahn;
import static com.helfholz.muetze187.vatertag2017.BlauzahnActivity.mReceiverBlauZahn;
import static com.helfholz.muetze187.vatertag2017.BlauzahnActivity.textViewinfo;
import static com.helfholz.muetze187.vatertag2017.MainActivity.adapterTeamList;
import static com.helfholz.muetze187.vatertag2017.MainActivity.alarmsOff;
import static com.helfholz.muetze187.vatertag2017.MainActivity.empfangen;
import static com.helfholz.muetze187.vatertag2017.MainActivity.filter;
import static com.helfholz.muetze187.vatertag2017.MainActivity.listViewTeams;
import static com.helfholz.muetze187.vatertag2017.MainActivity.mReceiver;
import static com.helfholz.muetze187.vatertag2017.MainActivity.teamList;


/**
 * Created by Muetze187 on 20.11.2016.
 */

public class BluetoothHandler extends Application{
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket socket = null;
    private OutputStream stream_out = null;
    private InputStream stream_in = null;
    private boolean is_connected = false;
    private boolean is_bt_on = false;
    //private static String mac_adresse = "20:15:01:29:11:32";
    private static String mac_adresse = "98:D3:32:10:79:C6";
    private BluetoothDevice remoteDevice;
    private static BluetoothHandler handlerBT = null;
    Context context;
    private BluetoothHandler(Context context){
        this.context = context;
    }

    public static BluetoothHandler getHandlerBT(Context context){
        if(handlerBT == null){

            handlerBT = new BluetoothHandler(context);
        }
        return handlerBT;
    }

    public synchronized BluetoothAdapter getBtAdapter(){
        if(btAdapter == null){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
            return btAdapter;

    }

    public synchronized BluetoothDevice getRemoteDevice(String adress){
        if(remoteDevice == null){
            mac_adresse = adress;
            remoteDevice = btAdapter.getRemoteDevice(mac_adresse);
        }
        return remoteDevice;
    }

    public synchronized BluetoothSocket getSocket(){
        if(socket == null){
            try {
                socket = getRemoteDevice(mac_adresse).createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    public synchronized void setSocket(BluetoothSocket socket){
        this.socket = socket;
    }

    public synchronized boolean getIsConnected(){
        return is_connected;
    }
    public synchronized boolean getIsBtOn(){
        return is_bt_on;
    }

    public synchronized void setIs_connected(boolean state){
        is_connected = state;
    }
    public synchronized void setIsBtOn(boolean state){
        is_bt_on = state;
    }

    public synchronized InputStream getStream_in(){
        if(stream_in == null){
            try {
                stream_in = getSocket().getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stream_in;
    }

    public synchronized OutputStream getStream_out(){
        if(stream_out == null){
            try {
                stream_out = getSocket().getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stream_out;
    }

    public void setMac_adresse(String mac){
        mac_adresse = mac;
    }

    public String getMac_adresse(){
        return mac_adresse;
    }


    public synchronized void connectBT(String adress) {
        if (getIsBtOn()) {
            if (getIsConnected()) {
                textViewinfo.setText("Bereits verbunden");
            } else {
                //textViewinfo.setText("Verbinde...");
                setMac_adresse(adress);
                socket = getSocket();
                btAdapter.cancelDiscovery();
                //remoteDevice = handlerBT.getRemoteDevice(adress);

                    new Thread(new Runnable(){
                        public void run(){
                            try {
                                socket.connect();
                                if(socket.isConnected())
                                    setIs_connected(true);
                                    //textViewinfo.setText("Verbunden");
                            } catch (IOException e) {
                                //textViewinfo.setText("Fehler - Abruch");
                                e.printStackTrace();
                                setIs_connected(false);
                            }
                        }
                    }).start();


                    registerReceiver(mReceiver, filter);
                    registerReceiver(mReceiverBlauZahn, filterBlauZahn);

                    //textViewinfo.setText("Socket kann nicht verbinden");




                if (!getIsConnected()) {
                    try {
                        socket.close();
                        textViewinfo.setText("Nicht verbunden");
                    } catch (IOException e) {
                        // textViewinfo.setText("Socket kann nicht geschlossen werden");
                        e.printStackTrace();
                    }
                }
            }

        } else {
            textViewinfo.setText("Bluetooth ist aus...");
        }
    }

    public synchronized void disconnectBT(){

        if(getIsConnected()){
           // try {
            if(stream_in != null){
                try {
                    stream_in.close();
                    stream_in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(stream_out != null){
                try {
                    stream_out.close();
                    stream_out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {
                getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setSocket(null);
                setIs_connected(false);
                alarmsOff();
                textViewinfo.setText("Verbindung getrennt");
           // } //catch (IOException e) {
                //Log.d(LOG_TAG_DRILLO, "Trennen nicht möglich! " + e.toString());
            //}
       // }else{
            //Log.d(LOG_TAG_DRILLO, "Nicht verbunden!");
        }

    }

    public void discover(){
        if(getBtAdapter().isDiscovering()){
            getBtAdapter().cancelDiscovery();
            textViewinfo.setText("Discovery gestoppt");
        }
        else{
            if(getBtAdapter().isEnabled()) {
                btArrayAdapter.clear(); // clear items
                getBtAdapter().startDiscovery();
                textViewinfo.setText("Discovery gestartet");
                context.getApplicationContext().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                textViewinfo.setText("Bluetooth ist aus");
            }
        }
    }

    final static BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void sendBT(String message) {

        if(handlerBT.getIsBtOn()){
            byte[] msgBuffer = message.getBytes();
            //tmp_stream_out = handlerBT.getStream_out();
            if (getIsConnected()) {
                textViewinfo.setText("Sende Nachricht " + message);
                try {
                    getStream_out().write(msgBuffer);
                    Log.e("tatsaechlich geht raus: ", message);
                } catch (IOException e) {
                    textViewinfo.setText("Fehler beim Senden");

                }
            }
            else{
                textViewinfo.setText("Nicht verbunden");
            }
        }else{
            textViewinfo.setText("Bluetooth einschalten");
        }


    }


    /*public void saveTeams(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(teamList);
        editor.putString("Teams", json);
        editor.commit();
        Toast.makeText(this, "Teams erfolgreich gespeichert", Toast.LENGTH_SHORT).show ();
        for(int i = 0; i < teamList.size(); i++)
            Log.d("saved ", "teams " + teamList.get(i).getName() +"JSON: " + json);
    }

    public void loadTeams(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("Teams",null);
        if(json != null){
            Type type = new TypeToken<ArrayList<Teams>>(){}.getType();
            teamList = gson.fromJson(json, type);
        }
        Toast.makeText(this, "Teams erfolgreich geladen", Toast.LENGTH_SHORT).show ();
        Log.d("teamListLoad", "TeamList " +teamList.size()+ " JSON " + json);
        //teamList = tmp;
        adapterTeamList = new CustomListAdapter(context, teamList);

        listViewTeams.setAdapter(adapterTeamList);
        adapterTeamList.notifyDataSetChanged();
    }*/


}
