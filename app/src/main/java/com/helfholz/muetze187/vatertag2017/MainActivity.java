package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static com.helfholz.muetze187.vatertag2017.BlauzahnActivity.textViewinfo;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Gui Elements
    boolean successBT = false;
    static boolean alertOn = true;
    static boolean isOneAlarmed = false;
    static String msg = "";
    Switch switchAlarm;
    TextView textViewDateTime;
    static TextView textFett;
    TextView adelheid;
    static ListView listViewTeams;
    TextView textViewAmountDrink;
    TextView textViewGlueckwunsch;
    TextView textViewNameChoose;
    TextView distanceMarkers;
    EditText editTextMAC;
    TextView textViewReceive;
    final String DRINKAMOUNT = "Zu trinkende Menge: ";
    int currentSong;
    static CustomListAdapter adapterTeamList;
    ArrayAdapter<String> adapterSpinner;
    static ArrayList<Teams> teamList = new ArrayList<Teams>();

    ArrayList<String> music = new ArrayList<String>();
    ArrayList<String> musicTrimmed = new ArrayList<String>();

    ImageButton play, prev, forw, shuffle;
    static SeekBar seekBarMusic;
    final int delay = 1000;
    int delay2 = 2000;
    Handler hTimeDate;
    static Handler hMusic;
    Handler hBlinzeln;
    Handler hAlert;
    Handler checkBTstate;
    Handler checkIncoming;
    static boolean isStarted;
    static boolean fromIntent;
    boolean isShuffle;
    Date date;
    SimpleDateFormat dateFormat;
    String s;
    Button buttonRandomDrink;
    Button btON, btOFF, btConnect;
    Button drink1, drink2, drink3;
    static MediaPlayer mediaPlayerMusic;
    MediaPlayer mp;
    //TESTS
    int drink;
    int oldDrink = -1;
    int hasChoosen = 0;
    int tmp;
    int amountToDrink = 2;
    int progress;
    int counter = 0;
    int winnerDrink;
    SharedPreferences prefs;
    Gson gson;
    String jsonTeams = "";
    boolean hadChanceDrink1 = false, isHadChanceDrink2 = false, isHadChanceDrink3 = false;
    Spinner spinner;
    Dialog dialogChangeName;
    Dialog dialogNewTeam;
    Dialog dialogTeamChoice;
    Dialog dialogDeleteTeam;
    Dialog dialogChooseDrink;
    Dialog dialogDrinkAccepted;
    Dialog dialogBT;
    ImageView imageViewPigOpenEyes, imageViewPigClosedEyes;
    ImageView imageViewDrink1, imageViewDrink2, imageViewDrink3;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    public static AudioManager audioManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    Marker markerGoal, markerStart;
    Location mLastLocation;
    LocationRequest locationRequest;
    private GoogleMap mMap;
    boolean mapCentered = false;
    private Switch switchMap;
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Animation animShake;
    Thread thread, threadReceive;
    //BT
    private static final String LOG_TAG_DRILLO = "Drillo";
    private static OutputStream stream_out = null;
    private static InputStream stream_in = null;
    static BluetoothHandler handlerBT;
    Button buttonHeartbeatDrillo;
    static IntentFilter filter;
    static String receivedMessage = "";
    int teamPos;
    int originalVolume;
    private SoundPool soundPool;
    int soundAlarm;
    int volume;
    int position2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = null;
        if (resultCode == 200) {

            name = data.getExtras().getString("filename");
            play.setBackgroundResource(R.drawable.pausecircularbutton);
            if (!name.isEmpty()) {
                int spinnerPos = adapterSpinner.getPosition("/TD/" + name);
                for (int i = 0; i < music.size(); i++) {
                    String tmp = music.get(i).toString();
                    if (tmp.contains(name)) {
                        spinner.setSelection(i);
                        currentSong = i;
                    }
                }

                isStarted = true;
                fromIntent = true;
            }

        }
        Log.d("name ist", name);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();
                    Intent i = new Intent(MainActivity.this, Mp3Activity.class);


                    startActivityForResult(i, 200);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    // consider as something else - a screen tap for example
                }
                break;

        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide statusbar
        hideStatusBar();

        //set layout
        setContentView(R.layout.activity_main);

        //force landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Maps
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        //init GUI
        textViewDateTime = (TextView) findViewById(R.id.textViewDateTime);
        textFett = (TextView) findViewById(R.id.textViewFett);
        distanceMarkers = (TextView) findViewById(R.id.textViewDistance);

        adelheid = (TextView) findViewById(R.id.textView2);
        play = (ImageButton) findViewById(R.id.play);
        prev = (ImageButton) findViewById(R.id.prev);
        forw = (ImageButton) findViewById(R.id.next);
        shuffle = (ImageButton) findViewById(R.id.shuffle);
        listViewTeams = (ListView) findViewById(R.id.listViewTeams);
        isStarted = false;
        fromIntent = false;
        isShuffle = false;
        getListData();
        animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);

        getMusic();
        for (String a : music) {
            musicTrimmed.add(a.substring(25));
        }


        //TESTS
        spinner = (Spinner) findViewById(R.id.spinner);
        adapterSpinner = new ArrayAdapter<String>(this,
                R.layout.spinner_item, musicTrimmed);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapterSpinner);
        //helps but depricated
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
        //Dialogs
        dialogChangeName = new Dialog(MainActivity.this);
        dialogChangeName.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeName.setContentView(R.layout.team_dialog_changename);
        dialogChangeName.setCanceledOnTouchOutside(true);
        dialogChangeName.setCancelable(true);
        dialogChangeName.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNewTeam = new Dialog(MainActivity.this);
        dialogNewTeam.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNewTeam.setContentView(R.layout.team_dialog_changename);
        dialogNewTeam.setCancelable(true);
        dialogNewTeam.setCanceledOnTouchOutside(true);
        dialogNewTeam.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTeamChoice = new Dialog(MainActivity.this);
        dialogTeamChoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTeamChoice.setContentView(R.layout.team_dialog_choice);
        dialogTeamChoice.setCancelable(true);
        dialogTeamChoice.setCanceledOnTouchOutside(true);
        dialogTeamChoice.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDeleteTeam = new Dialog(MainActivity.this);
        dialogDeleteTeam.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDeleteTeam.setContentView(R.layout.team_dialog_delete);
        dialogDeleteTeam.setCanceledOnTouchOutside(true);
        dialogDeleteTeam.setCancelable(true);
        dialogDeleteTeam.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogChooseDrink = new Dialog(MainActivity.this);
        dialogChooseDrink.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChooseDrink.setContentView(R.layout.team_dialog_choose_drink);
        dialogChooseDrink.setCanceledOnTouchOutside(false);
        dialogChooseDrink.setCancelable(false);
        dialogChooseDrink.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDrinkAccepted = new Dialog(MainActivity.this);
        dialogDrinkAccepted.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDrinkAccepted.setContentView(R.layout.drink_acception);
        dialogDrinkAccepted.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBT = new Dialog(MainActivity.this);
        dialogBT.setContentView(R.layout.activity_blauzahn);

        //music seekbar
        seekBarMusic = (SeekBar) findViewById(R.id.seekBarMusic);
        seekBarMusic.setOnSeekBarChangeListener(this);
        seekBarMusic.getProgressDrawable().setColorFilter(Color.parseColor("#ffaaab"), android.graphics.PorterDuff.Mode.SRC_IN);

        //ImageViews
        imageViewPigClosedEyes = (ImageView) dialogTeamChoice.findViewById(R.id.imageViewPigClosedEyes);
        imageViewPigOpenEyes = (ImageView) dialogTeamChoice.findViewById(R.id.imageViewPigOpenEyes);
        imageViewPigClosedEyes.setVisibility(View.INVISIBLE);
        imageViewDrink1 = (ImageView) dialogChooseDrink.findViewById(R.id.imageViewChooseDrink1);
        imageViewDrink2 = (ImageView) dialogChooseDrink.findViewById(R.id.imageViewChooseDrink2);
        imageViewDrink3 = (ImageView) dialogChooseDrink.findViewById(R.id.imageViewChooseDrink3);


        progress = 0;

        //SOUNDS
        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundAlarm = soundPool.load(this,R.raw.foghorndanielsimon,1);

        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        volume = originalVolume / maxVol;

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);


        //buttonHeartbeatDrillo = (Button) findViewById(R.id.buttonFettACTION);

        adapterTeamList = new CustomListAdapter(this, teamList);


        listViewTeams.setAdapter(adapterTeamList);
        Log.d("liste1", music.get(0).toString());
        Log.d("listelast", music.get(music.size() - 1).toString());

        //init Handlers
        initHandlers();

        handlerBT = BluetoothHandler.getHandlerBT(this);
        checkBTstate = new Handler();
        checkIncoming = new Handler();
        checkBT();
        checkIncomingArduino.run();
        if (handlerBT.getBtAdapter().isEnabled()) {
            handlerBT.setIsBtOn(true);
        }


        mediaPlayerMusic = new MediaPlayer();
        mediaPlayerMusic.setOnCompletionListener(this);

        mp = MediaPlayer.create(this, R.raw.foghorndanielsimon);
        buttonRandomDrink = (Button) dialogChooseDrink.findViewById(R.id.buttonRandom);


        textViewDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOut();
            }
        });


        textFett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, BlauzahnActivity.class);
                startActivity(intent);

            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (mapCentered) {
                    mLastLocation = location;
                    Log.d("onLocationChangedOwweProvider", location.getLatitude() + "" + location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    Location start = new Location("");
                    start.setLatitude(latLng.latitude);
                    start.setLongitude(latLng.longitude);
                    Location finish = new Location("");
                    LatLng latLngFinish = markerGoal.getPosition();
                    finish.setLatitude(latLngFinish.latitude);
                    finish.setLongitude(latLngFinish.longitude);
                    float distance = (int) start.distanceTo(finish);
                    distanceMarkers.setText(distanceMarkers.getText().toString() + distance);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (mapCentered) {
                    mLastLocation = location;
                    Log.d("onLocationChangedOwweGPS", location.getLatitude() + "" + location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    Location start = new Location("");
                    start.setLatitude(latLng.latitude);
                    start.setLongitude(latLng.longitude);
                    Location finish = new Location("");
                    LatLng latLngFinish = markerGoal.getPosition();
                    finish.setLatitude(latLngFinish.latitude);
                    finish.setLongitude(latLngFinish.longitude);
                    float distance = (int) start.distanceTo(finish);
                    distanceMarkers.setText("Noch zu Wandern: " + distance);

                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        switchMap = (Switch) findViewById(R.id.switchMap);
        switchMap.setChecked(false);
        switchMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapCentered = true;
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } else {
                    mapCentered = false;
                }
            }
        });
        //TESTS

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("IM HERE! FOR NO REASON", "");
                try {
                    if (fromIntent) {
                        fromIntent = false;
                    } else if (!isStarted) {
                        currentSong = i;
                    } else {
                        playSong(music.get(i));
                        isStarted = true;
                        play.setBackgroundResource(R.drawable.pausecircularbutton);
                        currentSong = i;
                        fromIntent = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //OnClick-Listeners

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStarted) {
                    if (mediaPlayerMusic.isPlaying()) {
                        mediaPlayerMusic.pause();
                        play.setBackgroundResource(R.drawable.playcircularbutton);

                    } else {
                        mediaPlayerMusic.start();
                        play.setBackgroundResource(R.drawable.pausecircularbutton);
                    }
                    fromIntent = false;
                } else {
                    try {
                        playSong(music.get(currentSong));
                        isStarted = true;
                        fromIntent = false;
                        spinner.setSelection(currentSong);
                        play.setBackgroundResource(R.drawable.pausecircularbutton);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStarted) {

                } else {
                    if (currentSong == 0) {
                        mediaPlayerMusic.reset();
                        currentSong = music.size() - 1;
                        try {
                            playSong(music.get(currentSong));
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        currentSong--;
                        try {
                            playSong(music.get(currentSong));
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    play.setBackgroundResource(R.drawable.pausecircularbutton);
                }
                fromIntent = false;

            }

        });

        forw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStarted) {

                } else {
                    if (currentSong < (music.size() - 1)) {
                        mediaPlayerMusic.reset();
                        currentSong++;
                        try {
                            playSong(music.get(currentSong));
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        currentSong = 0;
                        try {
                            playSong(music.get(currentSong));
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    play.setBackgroundResource(R.drawable.pausecircularbutton);
                }
                fromIntent = false;

            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), FileExplorer.class);
                //startActivity(intent);
                fadeIn();
               /* if(isShuffle){
                    isShuffle = false;
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle);
                }
                else{
                    isShuffle = true;
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle_pressed);

                }

                Log.d("HELLO", String.valueOf(isShuffle));*/
            }
        });


        listViewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Object o = listViewTeams.getItemAtPosition(position);
                position2 = position;
                Button buttonChangeName = (Button) dialogTeamChoice.findViewById(R.id.buttonChangeName);
                Button buttonDeleteTeam = (Button) dialogTeamChoice.findViewById(R.id.buttonDelete);
                Button buttonNewTeam = (Button) dialogTeamChoice.findViewById(R.id.buttonNewTeam);
                textViewAmountDrink = (TextView) dialogChooseDrink.findViewById(R.id.textViewMengeDrink);
                textViewGlueckwunsch = (TextView) dialogChooseDrink.findViewById(R.id.textViewGlueckwunsch);
                textViewNameChoose = (TextView) dialogChooseDrink.findViewById(R.id.textViewNameChoose);

                buttonRandomDrink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.d("haschoosen","haschoosen beginn Mischen" +hasChoosen);

                        //hasChoosen++;
                        amountToDrink *= 2;
                        textViewAmountDrink.setText(DRINKAMOUNT + amountToDrink + " cl");

                        Log.d("drink", "drink beginn Mischen" + drink);
                        Log.d("drink", "OLDdrink beginn Mischen" + oldDrink);
                        Log.d("drink", "TMP beginn Mischen" + tmp);
                        Log.d("drink", "Amount beginn Mischen" + amountToDrink);

                        if (drink == 0)
                            imageViewDrink1.setColorFilter(Color.RED);
                        if (drink == 1)
                            imageViewDrink2.setColorFilter(Color.RED);
                        if (drink == 2)
                            imageViewDrink3.setColorFilter(Color.RED);
                      /*  if(hasChoosen == 1){
                            buttonRandomDrink.setText("Alternative");
                        }*/
                /*        if(hasChoosen < 2){
                            getRandomDrink();
                            buttonRandomDrink.setAnimation(animShake);
                        }
                        else
                        {
*/
                        getRandomDrink();
                        if ((tmp == 0 && drink == 1) || (tmp == 1 && drink == 0)) {
                            imageViewDrink1.setColorFilter(Color.RED);
                            imageViewDrink2.setColorFilter(Color.RED);
                            imageViewDrink3.setColorFilter(Color.GREEN);
                            imageViewDrink3.startAnimation(animShake);
                            imageViewDrink1.clearAnimation();
                            imageViewDrink2.clearAnimation();
                            winnerDrink = 3;
                        } else if ((tmp == 0 && drink == 2) || (drink == 0 && tmp == 2)) {
                            imageViewDrink1.setColorFilter(Color.RED);
                            imageViewDrink2.setColorFilter(Color.GREEN);
                            imageViewDrink3.setColorFilter(Color.RED);
                            imageViewDrink2.startAnimation(animShake);
                            imageViewDrink1.clearAnimation();
                            imageViewDrink3.clearAnimation();
                            winnerDrink = 2;
                        } else {
                            imageViewDrink1.setColorFilter(Color.GREEN);
                            imageViewDrink2.setColorFilter(Color.RED);
                            imageViewDrink3.setColorFilter(Color.RED);
                            imageViewDrink1.startAnimation(animShake);
                            imageViewDrink2.clearAnimation();
                            imageViewDrink3.clearAnimation();
                            winnerDrink = 1;
                        }

                        finishDialogChooseDrink(position);

                    }


                });
                if (teamList.get(position).getAlerted()) {
                    doTheShit();

                } else {

                    buttonChangeName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO BUG evtl, Tatstatu geht net hoch...
                            //dialogChangeName.setTitle("Teamname ändern");
                            Button buttonOK = (Button) dialogChangeName.findViewById(R.id.buttonChangeTeamOK);
                            final EditText editName = (EditText) dialogChangeName.findViewById(R.id.editTextChangeTeam);
                            final EditText editMemberOne = (EditText) dialogChangeName.findViewById(R.id.editTextChangeMemberOne);
                            final EditText editMemberTwo = (EditText) dialogChangeName.findViewById(R.id.editTextChangeMemberTwo);

                            dialogChangeName.setCanceledOnTouchOutside(true);
                            dialogChangeName.show();
                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String tmp = editName.getText().toString();
                                    String tmp2 = editMemberOne.getText().toString();
                                    String tmp3 = editMemberTwo.getText().toString();
                                    if (editName.getText().toString().equals("")){
                                        teamList.get(position).setName("no name selected");
                                    }
                                    else{
                                        teamList.get(position).setName(tmp);
                                    }

                                    if (editMemberOne.getText().toString().equals("")){
                                        teamList.get(position).setMemberOne("no name selected");
                                    }
                                    else{
                                        teamList.get(position).setMemberOne(tmp2);
                                    }

                                    if (editMemberTwo.getText().toString().equals("")){
                                        teamList.get(position).setMemberTwo("no name selected");
                                    }
                                    else{
                                        teamList.get(position).setMemberTwo(tmp3);
                                    }

                                    adapterTeamList.notifyDataSetChanged();
                                    editName.setText("");
                                    editMemberOne.setText("");
                                    editMemberTwo.setText("");
                                    dialogChangeName.dismiss();
                                }
                            });
                        }
                    });
                    buttonDeleteTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDeleteTeam.setTitle("Wirklich löschen");
                            Button buttonOK = (Button) dialogDeleteTeam.findViewById(R.id.buttonDeleteOk);
                            Button buttonCancel = (Button) dialogDeleteTeam.findViewById(R.id.buttonDeleteCancel);
                            dialogDeleteTeam.show();
                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    teamList.remove(position);
                                    adapterTeamList.notifyDataSetChanged();
                                    dialogDeleteTeam.dismiss();
                                    dialogTeamChoice.dismiss();
                                }
                            });
                            buttonCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogDeleteTeam.dismiss();

                                }
                            });

                        }
                    });

                    buttonNewTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogNewTeam.setTitle("Teamname wählen");
                            Button buttonOK = (Button) dialogNewTeam.findViewById(R.id.buttonChangeTeamOK);
                            final EditText editName = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeTeam);
                            final EditText editMemberOne = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeMemberOne);
                            final EditText editMemberTwo = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeMemberTwo);
                            dialogNewTeam.show();
                            imageViewPigClosedEyes.setImageResource(R.drawable.schwein);
                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Teams team = new Teams();
                                    String tmp = editName.getText().toString();
                                    String tmp2 = editMemberOne.getText().toString();
                                    String tmp3 = editMemberTwo.getText().toString();
                                    if (editName.getText().toString().equals("")){
                                        team.setName("no name selected");
                                    }
                                    else{
                                        team.setName(tmp);
                                    }
                                    if (editMemberOne.getText().toString().equals("")){
                                        team.setMemberOne("no name selected");
                                    }
                                    else{
                                        team.setMemberOne(tmp2);
                                    }

                                    if (editMemberTwo.getText().toString().equals("")){
                                        team.setMemberTwo("no name selected");
                                    }
                                    else{
                                        team.setMemberTwo(tmp3);
                                    }


                                    team.setDrunk(0);
                                    teamList.add(0, team);
                                    adapterTeamList.notifyDataSetChanged();
                                    editName.setText("");
                                    editMemberOne.setText("");
                                    editMemberTwo.setText("");
                                    dialogNewTeam.dismiss();

                                }
                            });
                        }
                    });

                    blizeldiewinzel();
                    dialogTeamChoice.show();

                }

            }
        });

        setupDateFormat();
        showTimeAndDate();
        alarmTeam();
        //checkBT();

    }

        public static void empfangen() {
            byte[] buffer = new byte[1024]; // Puffer
            int laenge; // Anzahl empf. Bytes

            stream_in = handlerBT.getStream_in();
            stream_out = handlerBT.getStream_out();
            try {
                if (stream_in.available() > 0) {
                    laenge = stream_in.read(buffer);
                    Log.d(LOG_TAG_DRILLO,
                            "Anzahl empfangender Bytes: " + String.valueOf(laenge));

                    // Message zusammensetzen:
                    for (int i = 0; i < laenge; i++)
                        msg += (char) buffer[i];

                    Log.d(LOG_TAG_DRILLO, "Message: " + msg);
                    //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    textViewinfo.setText(msg);

                } else{}
                    //Toast.makeText(this, "Nichts empfangen", Toast.LENGTH_LONG)
                            //.show();
            } catch (Exception e) {
                Log.e(LOG_TAG_DRILLO, "Fehler beim Empfangen: " + e.toString());
            }
        }

    private void saveTeams(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString("Teams",null);
        if(json != null){
            Type type = new TypeToken<ArrayList<Teams>>(){}.getType();
            teamList = gson.fromJson(json, type);
        }
        Toast.makeText(this, "Teams erfolgreich geladen", Toast.LENGTH_SHORT).show ();
        Log.d("teamListLoad", "TeamList " +teamList.size()+ " JSON " + json);
        //teamList = tmp;
        adapterTeamList = new CustomListAdapter(MainActivity.this, teamList);

        listViewTeams.setAdapter(adapterTeamList);
        adapterTeamList.notifyDataSetChanged();
    }

    ////////////////////////----------------------------\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\TODO

    private void doTheShit(){
        resetDrinkDialog();
//        Log.e("Name des Members", textViewNameChoose.getText().toString());
//        Log.e("MembersUno","" +teamList.get(position2).getMemberOne());
//        Log.e("MembersDuo",""+teamList.get(position2).getMemberTwo());
        if(textViewNameChoose.getText().toString().contains(teamList.get(position2).getMemberOne().toString())){
            textViewNameChoose.setText(teamList.get(position2).getMemberTwo().toString() + " @ " + teamList.get(position2).getName().toString());
        }else{
            textViewNameChoose.setText(teamList.get(position2).getMemberOne().toString() + " @ " + teamList.get(position2).getName().toString());
        }

        textViewAmountDrink.setText(DRINKAMOUNT + amountToDrink + " cl");
        textViewGlueckwunsch.setText("Glückwunsch! Dies ist euer " + teamList.get(position2).getDrunkPlain() + ". Schnaps!");
        //int drink = rnd.nextInt(3);

        getRandomDrink();
        oldDrink = drink;
        Log.d("drink", "drink initial" + drink);


        dialogChooseDrink.show();
        final Button buttonOK = (Button) dialogDrinkAccepted.findViewById(R.id.buttonDrinkOk);
        final Button buttonCancel = (Button) dialogDrinkAccepted.findViewById(R.id.buttonDrinkCancel);

        imageViewDrink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drink == 0) {
                    dialogDrinkAccepted.show();
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(), "Prost!", Toast.LENGTH_SHORT).show ();
                            dialogDrinkAccepted.dismiss();
                            winnerDrink = 1;
                            finishDialogChooseDrink(position2);
                        }
                    });
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDrinkAccepted.dismiss();
                        }
                    });

                }
            }
        });

        imageViewDrink2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drink == 1) {
                    dialogDrinkAccepted.show();
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(), "Prost!", Toast.LENGTH_SHORT).show ();
                            dialogDrinkAccepted.dismiss();
                            winnerDrink = 2;
                            finishDialogChooseDrink(position2);
                        }
                    });
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDrinkAccepted.dismiss();

                        }
                    });
                }
            }
        });

        imageViewDrink3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drink == 2) {
                    dialogDrinkAccepted.show();
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getApplicationContext(), "Prost!", Toast.LENGTH_SHORT).show ();
                            dialogDrinkAccepted.dismiss();
                            winnerDrink = 3;
                            finishDialogChooseDrink(position2);
                        }
                    });
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDrinkAccepted.dismiss();
                        }
                    });
                }
            }
        });

    }




    ////////////////////////----------------------------\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\TODO






    private void resetDrinkDialog(){
        hasChoosen = 0;
        drink = -1;
        imageViewDrink1.setColorFilter(Color.TRANSPARENT);
        imageViewDrink2.setColorFilter(Color.TRANSPARENT);
        imageViewDrink3.setColorFilter(Color.TRANSPARENT);
        tmp = -1;
        amountToDrink = 2;
        buttonRandomDrink.setEnabled(true);
        buttonRandomDrink.setText("Mischen");
        buttonRandomDrink.setClickable(true);
        imageViewDrink1.setEnabled(true);
        imageViewDrink2.setEnabled(true);
        imageViewDrink3.setEnabled(true);
    }

    private void finishDialogChooseDrink(int pos){
        teamPos = pos;
        buttonRandomDrink.setText("Prost!");
        buttonRandomDrink.setEnabled(false);
        imageViewDrink1.setEnabled(false);
        imageViewDrink2.setEnabled(false);
        imageViewDrink3.setEnabled(false);
        imageViewDrink1.clearAnimation();
        imageViewDrink2.clearAnimation();
        imageViewDrink3.clearAnimation();
        teamList.get(pos).increaseStrackLevel(10);
        teamList.get(pos).setDrunk(teamList.get(pos).getDrunkPlain());
        teamList.get(pos).setAlerted(false);
        isOneAlarmed = false;

        switch (winnerDrink){
            case 1:
                teamList.get(pos).setCounterOne(teamList.get(pos).getCounterOne() + 1);
                imageViewDrink1.setColorFilter(Color.GREEN);
                imageViewDrink2.setColorFilter(Color.RED);
                imageViewDrink3.setColorFilter(Color.RED);
                if(amountToDrink == 2)
                    handlerBT.sendBT("AS.1_2#");
                else
                    handlerBT.sendBT("AS.1_4#");
                Log.e("drink 1 wird geschickt","" + amountToDrink + " cl");
                break;
            case 2:
                teamList.get(pos).setCounterTwo(teamList.get(pos).getCounterTwo() + 1);
                imageViewDrink1.setColorFilter(Color.RED);
                imageViewDrink2.setColorFilter(Color.GREEN);
                imageViewDrink3.setColorFilter(Color.RED);
                if(amountToDrink == 2)
                    handlerBT.sendBT("AS:2_2#");
                else
                    handlerBT.sendBT("AS:2_4#");
                Log.e("drink 2 wird geschickt","" + amountToDrink + " cl");
                break;
            case 3:
                teamList.get(pos).setCounterThree(teamList.get(pos).getCounterThree() + 1);
                imageViewDrink1.setColorFilter(Color.RED);
                imageViewDrink2.setColorFilter(Color.RED);
                imageViewDrink3.setColorFilter(Color.GREEN);
                if(amountToDrink == 2)
                    handlerBT.sendBT("AS:3_2#");
                else
                    handlerBT.sendBT("AS:3_4#");
                Log.e("drink 3 wird geschickt","" + amountToDrink + " cl");
                break;
        }

        new CountDownTimer(5000, 1000) {
            int tmp = 0;
            @Override
            public void onTick(long millisUntilFinished) {

               /* if(msg.contains("12 ausgeschenkt")){
                    tmp = 1;
                }else if(msg.contains("22 ausgeschenkt")){
                    tmp = 2;
                }else if(msg.contains("22 ausgeschenkt")){
                    tmp = 3;
                }*/
            }

            @Override
            public void onFinish() {
                // TODO ALLE Werte erst hier setzen mit switch/case

                //if(msg.contains("AS:DONE#"))
                    doTheShit();
                    //Toast.makeText(getApplicationContext(), "JUHU!", Toast.LENGTH_SHORT).show ();

             /*   switch (tmp){
                    case 1:
                        teamList.get(teamPos).increaseStrackLevel(10);
                        teamList.get(teamPos).setDrunk(teamList.get(teamPos).getDrunkPlain());
                        teamList.get(teamPos).setAlerted(false);
                        teamList.get(teamPos).setCounterOne(teamList.get(teamPos).getCounterOne() + 1);
                        break;
                    case 2:
                        teamList.get(teamPos).increaseStrackLevel(10);
                        teamList.get(teamPos).setDrunk(teamList.get(teamPos).getDrunkPlain());
                        teamList.get(teamPos).setAlerted(false);
                        teamList.get(teamPos).setCounterTwo(teamList.get(teamPos).getCounterTwo() + 1);
                        break;
                    case 3:
                        teamList.get(teamPos).increaseStrackLevel(10);
                        teamList.get(teamPos).setDrunk(teamList.get(teamPos).getDrunkPlain());
                        teamList.get(teamPos).setAlerted(false);
                        teamList.get(teamPos).setCounterThree(teamList.get(teamPos).getCounterTwo() + 1);
                }*/

               /* if(tmp){
                    Toast.makeText(getApplicationContext(), "JUHU er sendet!", Toast.LENGTH_SHORT).show ();
                }*/
               //fadeIn();
                //dialogChooseDrink.dismiss();
                adapterTeamList.notifyDataSetChanged();
                msg = "";
                //tmp = false;

            }
        }.start();

    }

    private void getRandomDrink() {

        Random rnd = new Random();
        //int drink;
        tmp = drink;
        drink = rnd.nextInt(3);
        Log.d("drink","drink methode" +drink);
        if((drink == oldDrink)){
            getRandomDrink();
        }
        else{
            switch(drink){
                case 0:
                    imageViewDrink1.setImageResource(R.drawable.number1pink);
                    imageViewDrink2.setImageResource(R.drawable.number2white);
                    imageViewDrink3.setImageResource(R.drawable.number3white);
                    imageViewDrink1.startAnimation(animShake);
                    imageViewDrink2.clearAnimation();
                    imageViewDrink3.clearAnimation();
                    break;
                case 1:
                    imageViewDrink2.setImageResource(R.drawable.number2pink);
                    imageViewDrink1.setImageResource(R.drawable.number1white);
                    imageViewDrink3.setImageResource(R.drawable.number3white);
                    imageViewDrink2.startAnimation(animShake);
                    imageViewDrink1.clearAnimation();
                    imageViewDrink3.clearAnimation();
                    break;
                case 2:
                    imageViewDrink3.setImageResource(R.drawable.number3pink);
                    imageViewDrink1.setImageResource(R.drawable.number1white);
                    imageViewDrink2.setImageResource(R.drawable.number2white);
                    imageViewDrink3.startAnimation(animShake);
                    imageViewDrink1.clearAnimation();
                    imageViewDrink2.clearAnimation();
                    break;
            }


        }

        oldDrink = drink;
        //return drink;
    }


   /* private void checkBT(){
        checkBTstate.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (handlerBT.getIsConnected()) {
                   //Log.e("BT status","juhu");
                    //Log.d("isconnected  Main","->" + handlerBT.getIsConnected());
                    buttonHeartbeatDrillo.setBackgroundColor(Color.GREEN);
                }else{
                    buttonHeartbeatDrillo.setBackgroundColor(Color.RED);
                }
                checkBTstate.postDelayed(this, 1000);
            }


        }, 5000);
    }*/

    private void blizeldiewinzel() {
        hBlinzeln.postDelayed(new Runnable() {
            int zaehler = 0;

            @Override
            public void run() {
                Random random = new Random();
                delay2 = random.nextInt(4500 - 2500) + 2500;

                if (imageViewPigClosedEyes.getVisibility() == View.VISIBLE)
                    imageViewPigClosedEyes.setVisibility(View.INVISIBLE);
                else {
                    imageViewPigClosedEyes.setVisibility(View.VISIBLE);
                    delay2 = 160;
                }


                hBlinzeln.postDelayed(this, delay2);

            }
        }, delay2);
    }

    private void alarmTeam() {
        final int delay3 = 15000;

        hAlert.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                final int alert = rand.nextInt(teamList.size());
                toggleAlert(alert);
                hAlert.postDelayed(this, delay3);

            }
        }, delay3);
    }


    @Override
    public void onCompletion(MediaPlayer player) {

        if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSong = rand.nextInt((music.size() - 1) - 0 + 1) + 0;
            try {
                playSong(music.get(currentSong));
                spinner.setSelection(currentSong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSong < (music.size() - 1)) {
                try {
                    playSong(music.get(currentSong + 1));
                    spinner.setSelection(currentSong);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentSong += 1;
            } else {
                // play first song
                try {
                    playSong(music.get(0));
                    spinner.setSelection(currentSong);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentSong = 0;
            }

        }
    }

    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            saveTeams();
            Log.d("saveTEamsOnPause()", "saved");
            ex.printStackTrace();
        }
    };
    public void playSong(String path) throws IllegalArgumentException,
            IllegalStateException, IOException {
        // String extStorageDirectory = Environment.getExternalStorageDirectory()
        //        .toString() + "/Music/TD"; //add var folder to run on actual device!

        // path = extStorageDirectory + File.separator + path;
        //path = extStorageDirectory + path;

            Log.d("path:", path);
            mediaPlayerMusic.reset();
            mediaPlayerMusic.setDataSource(path);
            mediaPlayerMusic.prepare();
            mediaPlayerMusic.setVolume(100, 100);
            seekBarMusic.setProgress(0);
            seekBarMusic.setMax(100);
            mUpdateTimeTask.run();
            mediaPlayerMusic.start();


    }


    protected void showTimeAndDate() {
        hTimeDate.postDelayed(new Runnable() {
            @Override
            public void run() {
                date = new Date();
                s = dateFormat.format(date);
                textViewDateTime.setText(s);
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                hTimeDate.postDelayed(this, delay);
            }
        }, delay);
    }

    protected void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void initHandlers() {
        hTimeDate = new Handler();
        hMusic = new Handler();
        hBlinzeln = new Handler();
        hAlert = new Handler();
        checkBTstate = new Handler();
    }

    protected void setupDateFormat() {
        TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(tz);
    }


    private void toggleAlert(int posList) {

        if(alertOn && !isOneAlarmed && handlerBT.getIsConnected()){
            teamList.get(posList).setAlerted(true);
            isOneAlarmed = true;
            fadeOut();
          /*  if (teamList.get(posList).getAlerted()) {
                teamList.get(posList).setAlerted(false);
                Collections.sort(teamList, Teams.teamsComparator);
            } else {
                teamList.get(posList).setAlerted(true);
                 mp.start();

            }*/

//            try {
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                r.play();
//                while(r.isPlaying()){
//
//                }
//                fadeIn();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            //mp.start(); //TODO soundpool
            soundPool.play(soundAlarm, volume ,volume,1,0,1f);



            //fadeIn();

            Collections.sort(teamList, Teams.teamsComparator);
            adapterTeamList.notifyDataSetChanged();
        }

    }

    private void getListData() {

        Teams team1 = new Teams();
        team1.setName("Error 404");
        team1.setMemberOne("Iggy");
        team1.setMemberTwo("Marc");
        team1.setDrunk(5);
        team1.setAlerted(false);
        teamList.add(team1);

        Teams team2 = new Teams();
        team2.setName("Grünviehs");
        team2.setMemberOne("Grüni");
        team2.setMemberTwo("Robbvieh");
        team2.setDrunk(23);
        team2.setAlerted(false);
        teamList.add(team2);

        Teams team3 = new Teams();
        team3.setName("Gagipenners");
        team3.setMemberOne("Gagi");
        team3.setMemberTwo("Achim");
        team3.setDrunk(-3);
        team3.setAlerted(false);

        teamList.add(team3);

        /*Teams team4 = new Teams();
        team4.setName("Digge");
        team4.setDrunk(33);
        team4.setAlerted(false);
        teamList.add(team4);

        Teams team5 = new Teams();
        team5.setName("Babbisch Guzel");
        team5.setDrunk(42);
        team5.setAlerted(false);
        teamList.add(team5);

        Teams team6 = new Teams();
        team6.setName("Spencer Hill");
        team6.setDrunk(11);
        team6.setAlerted(false);
        teamList.add(team6);

        Teams team7 = new Teams();
        team7.setName("Trump Voters");
        team7.setDrunk(88);
        team7.setAlerted(false);
        teamList.add(team7);

        Teams team8 = new Teams();
        team8.setName("Rock n`Roll");
        team8.setDrunk(66);
        team8.setAlerted(false);
        teamList.add(team8);

        Teams team9 = new Teams();
        team9.setName("Krobi Shinobi");
        team9.setDrunk(18);
        team9.setAlerted(false);
        teamList.add(team9);

        Teams team10 = new Teams();
        team10.setName("BWLer");
        team10.setAlerted(false);
        team10.setDrunk(0);
        teamList.add(team10);*/

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        hMusic.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        hMusic.removeCallbacks(mUpdateTimeTask);
        //int currentDuration = 0;


        int totalDuration = (int) (mediaPlayerMusic.getDuration() / 1000);
        int currentPosition = (int) ((((double) seekBar.getProgress()) / 100) * totalDuration);

        mediaPlayerMusic.seekTo(currentPosition * 1000);
        updateSeekBar();
    }

    private static Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            Double percentage = (double) 0;
            long totalDuration = mediaPlayerMusic.getDuration();
            long currentDuration = mediaPlayerMusic.getCurrentPosition();

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);
            percentage = (((double) currentSeconds) / totalSeconds) * 100;
            int progress = percentage.intValue();
            seekBarMusic.setProgress(progress);
            hMusic.postDelayed(this, 100);
        }
    };

    public void updateSeekBar() {
        hMusic.postDelayed(mUpdateTimeTask, 100);
    }


    public void getMusic() { //String[]
        //FOR ACTUAL DEVICE
    /*    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgsMp3 = new String[]{ mimeType};
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {String.valueOf(MediaStore.Audio.Media.DISPLAY_NAME)}, selectionMimeType , selectionArgsMp3,
                MediaStore.Audio.Media._ID);*/
        ContentResolver cr = this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.ARTIST_ID + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);

        int count = 0;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    music.add(data);
                    // Add code to get more column here

                    // Save to your list here
                }

            }
        }

        cur.close();



     /*   int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();*/

        // return songs;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        buildGoogleAPI();
        mMap.setMyLocationEnabled(true);

        markerGoal = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(49.227743, 7.480724))
                    .title("Scheierfeschd")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            markerStart = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(49.262674, 7.570136))
                    .title("Start Vatertag 2017")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(49.262674, 7.570136), 10);
        mMap.animateCamera(cameraUpdate);

        UiSettings settings =  mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setIndoorLevelPickerEnabled(false);
        settings.setMapToolbarEnabled(false);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this );
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
       /* mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
           // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            LatLng tmp = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            markerStart.setPosition(tmp);
        }*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Log.d("onLocationChangedUnne", location.getLatitude() + "" +location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Location start = new Location("");
        start.setLatitude(latLng.latitude);
        start.setLongitude(latLng.longitude);
        Location finish = new Location("");
        LatLng latLngFinish = markerGoal.getPosition();
        finish.setLatitude(latLngFinish.latitude);
        finish.setLongitude(latLngFinish.longitude);
        float distance = (int)start.distanceTo(finish);
        distanceMarkers.setText("Noch zu Wandern: " + distance);
        if(mGoogleApiClient != null)
             LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    private synchronized void buildGoogleAPI(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void checkBT() {
        checkBTstate.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (handlerBT.getIsConnected() == true) {
                    //btCheckArduino.setBackgroundColor(Color.GREEN);
                    textFett.clearAnimation();
                    textFett.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                } else {
                    //btCheckArduino.setBackgroundColor(Color.RED);
                    alarmCheffe(getApplicationContext());
                    for(Teams team:teamList){
                        team.setAlerted(false);
                    }
                    if(dialogChooseDrink.isShowing())
                        dialogChooseDrink.dismiss();
                    if(dialogDrinkAccepted.isShowing())
                        dialogDrinkAccepted.dismiss();

                }
                isOneAlarmed = false;
                adapterTeamList.notifyDataSetChanged();
                //TODO Check Antrieb
                checkBTstate.postDelayed(this, 1000);
            }


        }, 1000);
    }


    private Runnable checkIncomingArduino = new Runnable(){

            @Override
            public void run() {
                if(handlerBT.getIsConnected()){
                    empfangen();
                    Log.e("Message received:", msg );


                }
                /*if(msg.contains("12 ausgeschenkt")){
                    Toast.makeText(getApplicationContext(), "JUHU!", Toast.LENGTH_SHORT).show ();
                }*/
                checkIncoming.postDelayed(this, 1000);
            }

    };

   public static void alarmCheffe(Context context){
        //textFett.startAnimation(AnimationUtils.loadAnimation(context,android.R.anim.));
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.blink);
        textFett.setTextColor(Color.RED);
        textFett.startAnimation(animation);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //TODO Dinge ausstellen
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
        mGoogleApiClient.disconnect();
        unregisterReceiver(mReceiver);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if(mGoogleApiClient != null)
        mGoogleApiClient.connect();
        this.registerReceiver(mReceiver, filter);

    }


    @Override
    public void onStop() {
        super.onStop();

        //unregisterReceiver(mReceiver);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //this.unregisterReceiver(mReceiver);
        // TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mReceiver);
    }

    public final static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
                handlerBT.setIs_connected(true);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
                handlerBT.setIs_connected(false);
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
                handlerBT.setIs_connected(false);
            }
        }
    };


    private void fadeOut(){

        int targetVol = 2;
        int STEP_DOWN = 1;
        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //int orgVol = curVol;
        Log.e("vol", ""+curVol);
        while(curVol > targetVol) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVol - STEP_DOWN, 0);
            //mediaPlayerMusic.setVolume(curVol - STEP_DOWN, curVol - STEP_DOWN);
            curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

    }

    private void fadeIn(){

        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int STEP_UP = 1;

        while(curVol < originalVolume){

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVol + STEP_UP,0);
            //mediaPlayerMusic.setVolume(curVol + STEP_UP, curVol + STEP_UP);
            curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

    }

    public static void alarmsOff(){

        for(int i = 0; i < teamList.size(); i++){
                teamList.get(i).setAlerted(false);
        }
        adapterTeamList.notifyDataSetChanged();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}



class Mp3Filter implements FilenameFilter{

    @Override
    public boolean accept(File file, String s) {
        return (s.endsWith(".mp3"));
    }
}
