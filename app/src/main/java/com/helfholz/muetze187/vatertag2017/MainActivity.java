package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
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
import com.google.android.gms.vision.text.Text;
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
    ListView listViewMusic, listViewSearch;
    private ArrayList<String> item = null;
    private ArrayList<String> path = null;
    ArrayAdapter<String> fileList = null;
    ArrayAdapter<String> musicList = null;
     String fileName = "";
    TextView textViewAmountDrink;
    TextView textViewGlueckwunsch;
    TextView textViewNameChoose;
    TextView distanceMarkers;
    EditText editTextSearch;
    TextView textViewReceive;
    TextView textViewSong;

    final String DRINKAMOUNT = "Zu trinkende Menge: ";
    int currentSong;
    int currentPosition;
    static CustomListAdapter adapterTeamList;
    ArrayAdapter<String> adapterSpinner;
    static ArrayList<Teams> teamList = new ArrayList<Teams>();

    ArrayList<String> music = new ArrayList<String>();
    ArrayList<String> musicTrimmed = new ArrayList<String>();
    ArrayList<String> musicTrimmedSearch = new ArrayList<String>();
    ArrayList<String> musicTest = new ArrayList<String>();

    ImageButton play, prev, forw, shuffle, repeat;
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
    boolean isShuffle, isRepeat;
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
    //Spinner spinner;
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
    private String root = Environment.getExternalStorageDirectory().toString()+"/Music/" ;
    int zaehler = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if(listViewMusic.getVisibility() == View.INVISIBLE)
            listViewMusic.setVisibility(View.VISIBLE);
        listViewSearch.setVisibility(View.INVISIBLE);
        editTextSearch.setText("");
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

        textViewSong = (TextView) findViewById(R.id.textViewSong);
        textViewSong.setText("no song selected");



        adelheid = (TextView) findViewById(R.id.textView2);
        play = (ImageButton) findViewById(R.id.play);
        prev = (ImageButton) findViewById(R.id.prev);
        forw = (ImageButton) findViewById(R.id.next);
        shuffle = (ImageButton) findViewById(R.id.shuffle);
        repeat = (ImageButton) findViewById(R.id.repeat);
        listViewTeams = (ListView) findViewById(R.id.listViewTeams);
        listViewMusic = (ListView) findViewById(R.id.listViewMusic);
        listViewSearch = (ListView) findViewById(R.id.listViewSearch);
        listViewSearch.setVisibility(View.INVISIBLE);
        isStarted = false;
        fromIntent = false;
        isShuffle = false;
        isRepeat = false;
        getListData();
        animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);


        //for (String a : music) {
         //   musicTrimmed.add(a.substring(25));
        //}
        editTextSearch = (EditText) findViewById(R.id.search);

        //TESTS
        //spinner = (Spinner) findViewById(R.id.spinner);
        adapterSpinner = new ArrayAdapter<String>(this,
                R.layout.spinner_item, musicTrimmed);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);


        //spinner.setAdapter(adapterSpinner);
        //helps but depricated
        //spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
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
        //Log.d("liste1", music.get(0).toString());
        //Log.d("listelast", music.get(music.size() - 1).toString());

        //init Handlers
        initHandlers();
        musicList = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, musicTrimmedSearch );
        listViewSearch.setAdapter(musicList);

        handlerBT = BluetoothHandler.getHandlerBT(this);
        checkBTstate = new Handler();
        checkIncoming = new Handler();
        checkBT();
        //checkIncomingArduino.run();
        if (handlerBT.getBtAdapter().isEnabled()) {
            handlerBT.setIsBtOn(true);
        }


        mediaPlayerMusic = new MediaPlayer();
        //mediaPlayerMusic.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
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

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextSearch.getText().toString().equals("")){
                    listViewSearch.setVisibility(View.INVISIBLE);
                    listViewMusic.setVisibility(View.VISIBLE);
                }
                else{
                    listViewSearch.setVisibility(View.VISIBLE);
                    listViewMusic.setVisibility(View.INVISIBLE);
                }

                MainActivity.this.musicList.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
*/

        //OnClick-Listeners

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isStarted){

                }
                else if (isStarted) {
                    if (mediaPlayerMusic.isPlaying()) {
                        mediaPlayerMusic.pause();
                        //currentPosition = mediaPlayerMusic.getCurrentPosition();
                        play.setBackgroundResource(R.drawable.playcircularbutton);
                    }
                     else{
                        mediaPlayerMusic.start();
                        //mediaPlayerMusic.seekTo(currentPosition);
                        play.setBackgroundResource(R.drawable.pausecircularbutton);
                    }
                    fromIntent = false;
                } else {
                    try {
                        playSong(music.get(currentSong));
                        isStarted = true;
                        fromIntent = false;
                        //spinner.setSelection(currentSong);
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

                }else if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSong = rand.nextInt((music.size() - 1) - 0 + 1) + 0;
                    try {
                        playSong(music.get(currentSong));
                        textViewSong.setText(musicTrimmed.get(currentSong));
                        //spinner.setSelection(currentSong);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (currentSong == 0) {
                        mediaPlayerMusic.reset();
                        currentSong = music.size() - 1;
                        try {
                            playSong(music.get(currentSong));
                            textViewSong.setText(musicTrimmed.get(currentSong));
                            //spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        currentSong--;
                        try {
                            playSong(music.get(currentSong));
                            textViewSong.setText(musicTrimmed.get(currentSong));
                            //spinner.setSelection(currentSong);
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

                }
                else if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSong = rand.nextInt((music.size() - 1) - 0 + 1) + 0;
                    try {
                        playSong(music.get(currentSong));
                        //spinner.setSelection(currentSong);
                        textViewSong.setText(musicTrimmed.get(currentSong));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (currentSong < (music.size() - 1)) {
                        mediaPlayerMusic.reset();
                        currentSong++;
                        try {
                            playSong(music.get(currentSong));
                            textViewSong.setText(musicTrimmed.get(currentSong));
                            //spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        currentSong = 0;
                        try {
                            playSong(music.get(currentSong));
                            textViewSong.setText(musicTrimmed.get(currentSong));
                            //spinner.setSelection(currentSong);
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

               if(isShuffle){
                    isShuffle = false;
                    shuffle.setBackgroundResource(R.drawable.shuffle_big);
                    Collections.sort(music);
                }
                else{
                    isShuffle = true;
                    shuffle.setBackgroundResource(R.drawable.shufflebig_pushed_blue);
                    Collections.shuffle(music);
                }

                Log.d("HELLO", String.valueOf(isShuffle));
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRepeat){
                    isRepeat = false;
                    repeat.setBackgroundResource(R.drawable.repeat);
                }else{
                    isRepeat = true;
                    repeat.setBackgroundResource(R.drawable.repeat_pushed_blue);
                }
            }
        });

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File((String) listViewSearch.getItemAtPosition(position));
                textViewSong.setText(file.getName());
                Log.e("WÄSCHE",""+file.getName());
                try {
                    playSong("/storage/emulated/0/Music" +file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                File file = new File(path.get(i));


                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(path.get(i));
                }else{

                    fileName = file.getName();
                    isStarted = true;
                    play.setBackgroundResource(R.drawable.pausecircularbutton);
                    for (int j = 0; j < music.size(); j++) {
                        String tmp = music.get(j).toString();
                        if (tmp.contains(fileName)) {
                            //spinner.setSelection(j);
                            currentSong = j;
                        }
                    }
                    try {
                        //playSong(file.getAbsolutePath());
                        playSong(file.getAbsolutePath());
                        textViewSong.setText(musicTrimmed.get(currentSong));


                        //mainAtivity.playSong(file.getName() +".mp3");
                        //mainActivity.isStarted = true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // songIndex = i;
                    //Log.d("currentSong", "depp " + songIndex);
                }

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
                            editName.setText(teamList.get(position).getName());
                            final EditText editMemberOne = (EditText) dialogChangeName.findViewById(R.id.editTextChangeMemberOne);
                            editMemberOne.setText(teamList.get(position).getMemberOne());
                            final EditText editMemberTwo = (EditText) dialogChangeName.findViewById(R.id.editTextChangeMemberTwo);
                            editMemberTwo.setText(teamList.get(position).getMemberTwo());
                            final EditText editMemberThree = (EditText) dialogChangeName.findViewById(R.id.editTextChangeMemberThree);
                            editMemberThree.setText(teamList.get(position).getMemberThree());


                            dialogChangeName.setCanceledOnTouchOutside(true);
                            dialogChangeName.show();
                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String tmp = editName.getText().toString();
                                    String tmp2 = editMemberOne.getText().toString();
                                    String tmp3 = editMemberTwo.getText().toString();
                                    String tmp4 = editMemberThree.getText().toString();
                                    if (editName.getText().toString().equals("")){
                                        teamList.get(position).setName("no name");
                                    }
                                    else{
                                        teamList.get(position).setName(tmp);
                                    }

                                    if (editMemberOne.getText().toString().equals("")){
                                        teamList.get(position).setMemberOne("no name");
                                    }
                                    else{
                                        teamList.get(position).setMemberOne(tmp2);
                                    }

                                    if (editMemberTwo.getText().toString().equals("")){
                                        teamList.get(position).setMemberTwo("no name");
                                    }
                                    else{
                                        teamList.get(position).setMemberTwo(tmp3);
                                    }

                                    if (editMemberThree.getText().toString().equals("")){
                                        teamList.get(position).setMemberThree("no name");
                                    }
                                    else{
                                        teamList.get(position).setMemberThree(tmp4);
                                    }

                                    adapterTeamList.notifyDataSetChanged();
                                    editName.setText("");
                                    editMemberOne.setText("");
                                    editMemberTwo.setText("");
                                    editMemberThree.setText("");
                                    dialogChangeName.dismiss();
                                }
                            });
                            blizeldiewinzel();
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
                            blizeldiewinzel();
                        }
                    });

                    buttonNewTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogNewTeam.setTitle("Teamname wählen");
                            final boolean[] hasThreeMembers = {true};
                            Button buttonOK = (Button) dialogNewTeam.findViewById(R.id.buttonChangeTeamOK);
                            final EditText editName = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeTeam);
                            final EditText editMemberOne = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeMemberOne);
                            final EditText editMemberTwo = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeMemberTwo);
                            final EditText editMemberThree = (EditText) dialogNewTeam.findViewById(R.id.editTextChangeMemberThree);
                            final ImageView imageViewTeamSize2 = (ImageView) dialogNewTeam.findViewById(R.id.imageViewTeamsize2);
                            final ImageView imageViewTeamSize3 = (ImageView) dialogNewTeam.findViewById(R.id.imageViewTeamsize3);
                            final TextView textViewMember3 = (TextView) dialogNewTeam.findViewById(R.id.textViewChangeTeamMemberThree);
                            dialogNewTeam.show();
                            imageViewPigClosedEyes.setImageResource(R.drawable.schwein);

                            imageViewTeamSize2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageViewTeamSize2.setBackgroundResource(R.drawable.number2pink);
                                    imageViewTeamSize3.setBackgroundResource(R.drawable.number3white);
                                    editMemberThree.setVisibility(View.INVISIBLE);
                                    textViewMember3.setVisibility(View.INVISIBLE);
                                    hasThreeMembers[0] = false;
                                }
                            });

                            imageViewTeamSize3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageViewTeamSize2.setBackgroundResource(R.drawable.number2white);
                                    imageViewTeamSize3.setBackgroundResource(R.drawable.number3pink);
                                    editMemberThree.setVisibility(View.VISIBLE);
                                    textViewMember3.setVisibility(View.VISIBLE);
                                    hasThreeMembers[0] = true;
                                }
                            });

                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Teams team = new Teams(getApplicationContext());
                                    if(hasThreeMembers[0]){
                                        String tmp = editName.getText().toString();
                                        String tmp2 = editMemberOne.getText().toString();
                                        String tmp3 = editMemberTwo.getText().toString();
                                        String tmp4 = editMemberThree.getText().toString();
                                        if (editName.getText().toString().equals("")) {
                                            //team.setName("no name");
                                            tmp = "no name";
                                        }
                                        if (editMemberOne.getText().toString().equals("")){
                                            //team.setMemberOne("no name");
                                            tmp2 = "no name";
                                        }
                                        if (editMemberTwo.getText().toString().equals("")){
                                            //team.setMemberTwo("no name");
                                            tmp3 = "no name";
                                        }
                                        if (editMemberThree.getText().toString().equals("")){
                                            //team.setMemberThree("no name");
                                            tmp4 = "no name";
                                        }
                                        Teams team = new Teams(getApplicationContext(), tmp2,tmp3,tmp4);
                                        team.setName(tmp);
                                        team.setDrunk(0);
                                        teamList.add(0, team);
                                    }else{
                                        String tmp = editName.getText().toString();
                                        String tmp2 = editMemberOne.getText().toString();
                                        String tmp3 = editMemberTwo.getText().toString();
                                        if (editName.getText().toString().equals("")) {
                                            //team.setName("no name");
                                            tmp = "no name";
                                        }
                                        if (editMemberOne.getText().toString().equals("")){
                                            //team.setMemberOne("no name");
                                            tmp2 = "no name";
                                        }
                                        if (editMemberTwo.getText().toString().equals("")){
                                            //team.setMemberTwo("no name");
                                            tmp3 = "no name";
                                        }
                                        Teams team = new Teams(getApplicationContext(), tmp2,tmp3);
                                        team.setName(tmp);
                                        team.setDrunk(0);
                                        teamList.add(0, team);
                                    }
                                    adapterTeamList.notifyDataSetChanged();
                                    editName.setText("");
                                    editMemberOne.setText("");
                                    editMemberTwo.setText("");
                                    editMemberThree.setText("");
                                    dialogNewTeam.dismiss();

                                }
                            });
                            blizeldiewinzel();
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
        getDir(root);
        getMusic();
    }

        public static void empfangen() {
            msg = "";
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
        for(Teams team:teamList){
            team.setAlerted(false);
        }
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

    private void finishDialogChooseDrink(final int pos){
        teamPos = pos;
        buttonRandomDrink.setText("Prost!");
        buttonRandomDrink.setEnabled(false);
        imageViewDrink1.setEnabled(false);
        imageViewDrink2.setEnabled(false);
        imageViewDrink3.setEnabled(false);
        imageViewDrink1.clearAnimation();
        imageViewDrink2.clearAnimation();
        imageViewDrink3.clearAnimation();
        teamList.get(pos).increaseStrackLevel(5);
        teamList.get(pos).setDrunk(teamList.get(pos).getDrunkPlain());
        teamList.get(pos).setAlerted(false);


        switch (winnerDrink){
            case 1:
                teamList.get(pos).setCounterOne(teamList.get(pos).getCounterOne() + 1);
                imageViewDrink1.setColorFilter(Color.GREEN);
                imageViewDrink2.setColorFilter(Color.RED);
                imageViewDrink3.setColorFilter(Color.RED);
                if(amountToDrink == 2)
                    handlerBT.sendBT("AS:1_2#");
                else
                    handlerBT.sendBT("AS:1_4#");
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
        //TODO string richtig empfanen
        /*final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", msg, true);

        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                empfangen();
                while(!msg.equals("AS:DONE#")){

                }
                ringProgressDialog.dismiss();

            }
        }).start();*/
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if(zaehler == 0 ){
                    doTheShit();
                    Log.e("ZAEHLER == 0", "" + zaehler);
                }
                if(zaehler <3){
                    doTheShit();
                    Log.e("ZAEHLER == 1", "" + zaehler);
                }
                if(teamList.get(pos).getHasThreeMembers()){
                    doTheShit();
                    Log.e("ZAEHLER == 2", "" + zaehler);
                }
                else{
                    dialogChooseDrink.dismiss();
                    zaehler = 0;
                    isOneAlarmed = false;
                    adapterTeamList.notifyDataSetChanged();
                }
            }



        }.start();



    }

    private void doTheShit() {
        resetDrinkDialog();
//        Log.e("Name des Members", textViewNameChoose.getText().toString());
//        Log.e("MembersUno","" +teamList.get(position2).getMemberOne());
//        Log.e("MembersDuo",""+teamList.get(position2).getMemberTwo());

        if (zaehler == 0) {
            textViewNameChoose.setText(teamList.get(position2).getMemberOne().toString() + " @ " + teamList.get(position2).getName().toString());
            Log.e("doshit ZAEHLER == 0", "" + zaehler);
        } else if (zaehler < 3) {
            textViewNameChoose.setText(teamList.get(position2).getMemberTwo().toString() + " @ " + teamList.get(position2).getName().toString());
            Log.e("doshit ZAEHLER == 1", "" + zaehler);
        } else if (teamList.get(position2).getHasThreeMembers()) {
            textViewNameChoose.setText(teamList.get(position2).getMemberThree().toString() + " @ " + teamList.get(position2).getName().toString());
            Log.e("doshit ZAEHLER == 2", "" + zaehler);
        }

        textViewAmountDrink.setText(DRINKAMOUNT + amountToDrink + " cl");
        textViewGlueckwunsch.setText("Glückwunsch! Dies ist euer " + teamList.get(position2).getDrunkPlain() + ". Schnaps!");
        //int drink = rnd.nextInt(3);

        getRandomDrink();
        oldDrink = drink;
        Log.d("drink", "drink initial" + drink);
        zaehler++;

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
        final int delay3 = 12000;

        hAlert.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(alertOn && !isOneAlarmed && handlerBT.getIsConnected()) {
                    Random rand = new Random();
                    final int alert = rand.nextInt(teamList.size());
                    toggleAlert(alert);
                }

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
                textViewSong.setText(musicTrimmed.get(currentSong));
                //spinner.setSelection(currentSong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(isRepeat){
            try {
                playSong(music.get(currentSong));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            // no repeat or shuffle ON - play next song
            if (currentSong < (music.size() - 1)) {
                try {
                    playSong(music.get(currentSong + 1));
                    textViewSong.setText(musicTrimmed.get(currentSong + 1));
                    //spinner.setSelection(currentSong + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentSong += 1;
            } else {
                // play first song
                try {
                    playSong(music.get(0));
                    textViewSong.setText(musicTrimmed.get(0));
     //               spinner.setSelection(0);
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
                    Log.e("msg unne",msg);
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
        dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm:ss");
        dateFormat.setTimeZone(tz);
    }


    private void toggleAlert(int posList) {


            teamList.get(posList).setAlerted(true);
            isOneAlarmed = true;
            //handlerBT.sendBT("ALARM#");
            //fadeOut();
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

    private void getListData() {

        Teams team1 = new Teams(getApplicationContext(), "Iggy", "Marc");
        team1.setName("Error 404");
        team1.setDrunk(5);
        teamList.add(team1);

        Teams team2 = new Teams(getApplicationContext(), "Grüni", "Robbvieh");
        team2.setName("Grünviehs");
        team2.setDrunk(23);
        teamList.add(team2);

        Teams team3 = new Teams(getApplicationContext(), "Gagi", "Achim");
        team3.setName("Gagipenners");
        team3.setDrunk(-3);
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
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgsMp3 = new String[]{ mimeType};
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {String.valueOf(MediaStore.Audio.Media.DISPLAY_NAME)}, selectionMimeType , selectionArgsMp3,
                MediaStore.Audio.Media._ID);
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
                        musicTest.add(data);


                    // Add code to get more column here

                    // Save to your list here
                }

            }
        }
        for (String a : musicTest) {
            musicTrimmedSearch.add(a.substring(25));
        }
        cur.close();
        for(int i = 0; i < musicTest.size(); i++)
            Log.e("music unne","" + musicTest.get(i));

        for(int i = 0; i < musicTrimmedSearch.size(); i++)
            Log.e("music unne","" + musicTrimmedSearch.get(i));



       /* int count = mCursor.getCount();

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
                    isOneAlarmed = false;
                }
                adapterTeamList.notifyDataSetChanged();
                //TODO Check Antrieb
                checkBTstate.postDelayed(this, 1000);
            }


        }, 1000);
    }


    /*private Runnable checkIncomingArduino = new Runnable(){

            @Override
            public void run() {
                if(handlerBT.getIsConnected()){
                    empfangen();
                    Log.e("Message received:", msg );


                }
                *//*if(msg.contains("12 ausgeschenkt")){
                    Toast.makeText(getApplicationContext(), "JUHU!", Toast.LENGTH_SHORT).show ();
                }*//*
                checkIncoming.postDelayed(this, 100);
            }

    };*/

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
        // TODO music kracht!
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

    private void getDir(String dirPath) {
        //myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();


        File f = new File(dirPath);
        File[] files = f.listFiles();


        if(!dirPath.equals(root)) {
            item.add("zurück");
            path.add(root);
            music = new ArrayList<String>();
            musicTrimmed = new ArrayList<String>();
        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];
            path.add(file.getPath());
            if(file.isDirectory())
                item.add(file.getName() + "/");
            else{
                item.add(file.getName());
                music.add(file.getAbsolutePath());
            }


        }

        for (String a : music) {
           musicTrimmed.add(a.substring(25));
        }

        fileList = new ArrayAdapter<String>(this, R.layout.spinner_item, item);
        listViewMusic.setAdapter(fileList);


    }

}



