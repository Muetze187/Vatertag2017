package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;


import static com.helfholz.muetze187.vatertag2017.BluetoothHandler.blReceiver;
import static com.helfholz.muetze187.vatertag2017.MainActivity.adapterTeamList;

import static com.helfholz.muetze187.vatertag2017.MainActivity.alertOn;
import static com.helfholz.muetze187.vatertag2017.MainActivity.empfangen;
import static com.helfholz.muetze187.vatertag2017.MainActivity.isOneAlarmed;
import static com.helfholz.muetze187.vatertag2017.MainActivity.msg;
import static com.helfholz.muetze187.vatertag2017.MainActivity.teamList;
import static com.helfholz.muetze187.vatertag2017.MainActivity.listViewTeams;


public class BlauzahnActivity extends AppCompatActivity {

    Button btOn, btOff, btDisconnect, btDrink1, btDrink2, btDrink3, btShowList, btSaveTeams, btLoadTeams;
    Button btCheckAntrieb;
    static Button btCheckArduino;
    static Switch switchAlarm;

    static TextView textViewinfo ;
    static ArrayAdapter<String> btArrayAdapter;
    Set<BluetoothDevice> pairedDevices;
    ListView listViewDevices;
    private final static int REQUEST_ENABLE_BT = 1;
    Dialog dialogSaveTeams, dialogLoadTeams;
    private static BluetoothHandler handlerBTBlau;
    Handler checkBTstate;
    static IntentFilter filterBlauZahn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.schwein);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        //LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = inflator.inflate(R.layout.action_bar, null);
        View v = getLayoutInflater().inflate(R.layout.action_bar, null);
        actionBar.setCustomView(v);*/
        setContentView(R.layout.activity_blauzahn);

        btOn = (Button) findViewById(R.id.buttonWartungBTon);
        btOff = (Button) findViewById(R.id.buttonWartungBToff);
        btDisconnect = (Button) findViewById(R.id.buttonWartungBTtrennen);
        btDrink1 = (Button) findViewById(R.id.buttonWartungDrink1);
        btDrink2 = (Button) findViewById(R.id.buttonWartungDrink2);
        btDrink3 = (Button) findViewById(R.id.buttonWartungDrink3);
        btShowList = (Button) findViewById(R.id.buttonWartungBTShowList);
        btCheckArduino = (Button) findViewById(R.id.buttonFeedBackDrillo);
        btCheckAntrieb = (Button) findViewById(R.id.buttonFeedBackAntrieb);
        btSaveTeams = (Button) findViewById(R.id.buttonWartungSpeichern);
        btLoadTeams = (Button) findViewById(R.id.buttonWartungLaden);
        switchAlarm = (Switch) findViewById(R.id.switchWartungToggleAlarm);
        switchAlarm.setChecked(alertOn);
        listViewDevices = (ListView) findViewById(R.id.listViewBondedDevices);
        textViewinfo = (TextView) findViewById(R.id.textViewWartungBTinfo);
        checkBTstate = new Handler();

        dialogSaveTeams =new Dialog(BlauzahnActivity.this);
        dialogSaveTeams.setContentView(R.layout.save_teams);
        dialogLoadTeams = new Dialog(BlauzahnActivity.this);
        dialogLoadTeams.setContentView(R.layout.load_teams);

        handlerBTBlau = BluetoothHandler.getHandlerBT(this);

        filterBlauZahn = new IntentFilter();
        filterBlauZahn.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterBlauZahn.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filterBlauZahn.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiverBlauZahn, filterBlauZahn);

        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnBT();
            }
        });

        btOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffBT();
            }
        });

        btShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(handlerBTBlau.getBtAdapter().isEnabled()){
                    handlerBTBlau.discover();
                    listPairedDevices();
                    if(listViewDevices.getVisibility() == View.INVISIBLE)
                        listViewDevices.setVisibility(View.VISIBLE);
                }else{
                    textViewinfo.setText("Bluetooth ist aus...");
                }

            }
        });

        btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               handlerBTBlau.disconnectBT();

            }
        });

        btDrink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerBTBlau.sendBT("AS:1_2#");



            }
        });

        btDrink2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerBTBlau.sendBT("AS:2_2#");

            }
        });

        btDrink3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerBTBlau.sendBT("AS:3_2#");

            }
        });

        btSaveTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btJaSave = (Button) dialogSaveTeams.findViewById(R.id.buttonJaSpeichern);
                Button btNeinSave = (Button) dialogSaveTeams.findViewById(R.id.buttonNeinSpeichern);
                dialogSaveTeams.show();
                btJaSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveTeams();
                        dialogSaveTeams.dismiss();
                    }
                });
                btNeinSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogSaveTeams.dismiss();
                    }
                });

            }
        });

        btLoadTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btJaLoad = (Button) dialogLoadTeams.findViewById(R.id.buttonJaLaden);
                Button btNeinLoad = (Button) dialogLoadTeams.findViewById(R.id.buttonNeinLaden);
                dialogLoadTeams.show();
                btJaLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadTeams();
                        dialogLoadTeams.dismiss();
                    }
                });
                btNeinLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogLoadTeams.dismiss();
                    }
                });
            }
        });

        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(alertOn){
                   alertOn = false;
                   isOneAlarmed = false;
                   for(Teams team:teamList){
                       team.setAlerted(false);
                   }
                   switchAlarm.setChecked(false);
                   adapterTeamList.notifyDataSetChanged();
               }else{
                   alertOn = true;
                   switchAlarm.setChecked(true);
               }
            }
        });

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!handlerBTBlau.getBtAdapter().isEnabled()){
                    textViewinfo.setText("Bluetooth ist aus...");
                }
                String tmp = adapterView.getAdapter().getItem(i).toString();
                handlerBTBlau.setMac_adresse(tmp.substring(tmp.length() - 17));
                connect();
            }
        });



        if(handlerBTBlau.getBtAdapter().isEnabled()){
            textViewinfo.setText("Bluetooth an");
            handlerBTBlau.setIsBtOn(true);
        }else{
            textViewinfo.setText("Bluetooth ist aus");
            handlerBTBlau.setIsBtOn(false);
        }

        if(handlerBTBlau.getIsConnected())
            btCheckArduino.setBackgroundColor(Color.GREEN);
        //TODO ANTRIEB

        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewDevices.setAdapter(btArrayAdapter);


    }


    private void listPairedDevices(){
        pairedDevices = handlerBTBlau.getBtAdapter().getBondedDevices();
        if(handlerBTBlau.getBtAdapter().isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : pairedDevices)
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        else
            textViewinfo.setText("Bluetooth ist aus");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.unregisterReceiver(mReceiverBlauZahn);
    }

    private void turnOnBT(){
        if (handlerBTBlau.getBtAdapter() != null)
            if(!handlerBTBlau.getBtAdapter().isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                handlerBTBlau.setIsBtOn(true);

            }else{
                textViewinfo.setText("Bluetooth ist schon an");
            }
    }

    private void turnOffBT(){
        if(handlerBTBlau.getBtAdapter() != null)
            if(handlerBTBlau.getBtAdapter().isEnabled()){
                handlerBTBlau.getBtAdapter().disable();
                handlerBTBlau.setIs_connected(false);
                //Log.d("isconnected  Blauzahn","->" + handlerBTBlau.getIsConnected());
                handlerBTBlau.setIsBtOn(false);
                textViewinfo.setText("Bluetooth ist aus");
                listViewDevices.setVisibility(View.INVISIBLE);
            }else{
                textViewinfo.setText("Bluetooth ist schon aus");
            }
    }


    private void connect(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                handlerBTBlau.connectBT(handlerBTBlau.getMac_adresse());
            }

        });
        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       try {
            unregisterReceiver(blReceiver);
           unregisterReceiver(mReceiverBlauZahn);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                textViewinfo.setText("Bluetooth ist an");
            }
            else
                textViewinfo.setText("Bluetooth ist aus");
        }


    }

    private void saveTeams(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BlauzahnActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(teamList);
        editor.putString("Teams", json);
        editor.commit();
        Toast.makeText(this, "Teams erfolgreich gespeichert", Toast.LENGTH_SHORT).show ();
        for(int i = 0; i < teamList.size(); i++)
            Log.d("saved ", "teams " + teamList.get(i).getName() +"JSON: " + json);
    }

    private void loadTeams(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BlauzahnActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString("Teams",null);
        if(json != null){
            Type type = new TypeToken<ArrayList<Teams>>(){}.getType();
            teamList = gson.fromJson(json, type);
        }
        Toast.makeText(this, "Teams erfolgreich geladen", Toast.LENGTH_SHORT).show ();
        Log.d("teamListLoad", "TeamList " +teamList.size()+ " JSON " + json);
        //teamList = tmp;
        adapterTeamList = new CustomListAdapter(BlauzahnActivity.this, teamList);

        listViewTeams.setAdapter(adapterTeamList);
        adapterTeamList.notifyDataSetChanged();
    }

    public final static BroadcastReceiver mReceiverBlauZahn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
               handlerBTBlau.setIs_connected(true);
                btCheckArduino.setBackgroundColor(Color.GREEN);

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                handlerBTBlau.setIs_connected(false);
                btCheckArduino.setBackgroundColor(Color.RED);
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                handlerBTBlau.setIs_connected(false);
                btCheckArduino.setBackgroundColor(Color.RED);
            }
        }
    };


}