package com.helfholz.muetze187.vatertag2017;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static java.util.Collections.*;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    //Gui Elements
    TextView textViewDateTime;
    TextView textFett;
    TextView adelheid;
    ListView listViewTeams;
    ListView mListView;
    ImageView imageViewAlarm0;
    ImageView imageViewAlarm1;
    int currentSong;

    ArrayAdapter<String> mAdapter;
    CustomListAdapter adapterTeamList;

    ArrayList<Teams> teamList = new ArrayList<Teams>();

    ImageButton play, prev, forw, shuffle;
    SeekBar seekBarMusic;
    final static String FOLDER = "/music/";
    ArrayList image_details;
    final int delay = 1000;
    int delay2 = 2000;
    Handler hTimeDate;
    Handler hMusic;
    Handler hBlinzeln;
    Handler hAlert;
    boolean isStarted;
    boolean isShuffle;
    Date date;
    SimpleDateFormat dateFormat;
    String s;
    Button buttonLoad, buttonSave;
    private MediaPlayer mediaPlayerMusic;
    private String[] musicList;
    MediaPlayer mp;
    //TESTS
    ImageButton imageButton;
    Spinner spinner;
    private String m_Text = "";
    Dialog dialogChangeName;
    Dialog dialogNewTeam;
    Dialog dialogTeamChoice;
    Dialog dialogDeleteTeam;
    ImageView imageViewPigOpenEyes, imageViewPigClosedEyes;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public static AudioManager audioManager;
    //TeamSaver teamSaver;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSong = data.getExtras().getInt("songIndex");
            try {
                playSong(musicList[currentSong]);
                isStarted = true;
                play.setBackgroundResource(R.drawable.ic_pause);
                spinner.setSelection(currentSong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();
                    Intent i = new Intent(MainActivity.this, Mp3Activity.class);
                    i.putExtra("music", musicList);

                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else
                {
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
        //init GUI
        textViewDateTime = (TextView) findViewById(R.id.textViewDateTime);
        textFett = (TextView) findViewById(R.id.textViewFett);
        imageViewAlarm0 = (ImageView) findViewById(R.id.imageView);
        imageViewAlarm1 = (ImageView) findViewById(R.id.imageView2);
        imageViewAlarm1.setVisibility(View.INVISIBLE);
        imageViewAlarm0.setVisibility(View.INVISIBLE);
        adelheid = (TextView) findViewById(R.id.textView2);
        play = (ImageButton)findViewById(R.id.play);
        prev = (ImageButton) findViewById(R.id.prev);
        forw = (ImageButton) findViewById(R.id.next);
        shuffle = (ImageButton) findViewById(R.id.shuffle);
        listViewTeams = (ListView) findViewById(R.id.listViewTeams);
        mListView = (ListView) findViewById(R.id.listViewPlaylist);
        isStarted = false;
        isShuffle = false;
        image_details = getListData();
        musicList = getMusic();
        buttonLoad = (Button) findViewById(R.id.buttonLaden);
        buttonSave = (Button) findViewById(R.id.buttonSpeichern);
        //TESTS
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<String> adapterTest = new ArrayAdapter<String>(this,
                R.layout.spinner_item, musicList);
        adapterTest.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapterTest);
        //helps but depricated
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
        dialogChangeName = new Dialog(MainActivity.this);
        dialogChangeName.setContentView(R.layout.team_dialog_changename);
        dialogChangeName.setCanceledOnTouchOutside(true);
        dialogChangeName.setCancelable(true);
        dialogNewTeam = new Dialog(MainActivity.this);
        dialogNewTeam.setContentView(R.layout.team_dialog_changename);
        dialogNewTeam.setCancelable(true);
        dialogNewTeam.setCanceledOnTouchOutside(true);
        dialogTeamChoice = new Dialog(MainActivity.this);
        dialogTeamChoice.setContentView(R.layout.team_dialog_choice);
        dialogTeamChoice.setTitle("Wähle weise...");
        dialogTeamChoice.setCancelable(true);
        dialogTeamChoice.setCanceledOnTouchOutside(true);
        dialogDeleteTeam = new Dialog(MainActivity.this);
        dialogDeleteTeam.setContentView(R.layout.team_dialog_delete);
        dialogDeleteTeam.setCanceledOnTouchOutside(true);
        dialogDeleteTeam.setCancelable(true);

        //teamSaver = new TeamSaver(getApplicationContext());

        //music seekbar
        seekBarMusic = (SeekBar) findViewById(R.id.seekBarMusic);
        seekBarMusic.setOnSeekBarChangeListener(this);
        seekBarMusic.getProgressDrawable().setColorFilter(Color.parseColor("#ffaaab"),android.graphics.PorterDuff.Mode.SRC_IN);

        imageViewPigClosedEyes = (ImageView) dialogTeamChoice.findViewById(R.id.imageViewPigClosedEyes);
        imageViewPigOpenEyes = (ImageView) dialogTeamChoice.findViewById(R.id.imageViewPigOpenEyes);
        imageViewPigClosedEyes.setVisibility(View.INVISIBLE);

        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);

        hBlinzeln = new Handler();
        hAlert = new Handler();
        adapterTeamList = new CustomListAdapter(this, image_details);
        mAdapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, musicList);

        //mListView.setAdapter(mAdapter);
        listViewTeams.setAdapter(adapterTeamList);


        //init Handlers
        initHandlers();

        mediaPlayerMusic = new MediaPlayer();
        mediaPlayerMusic.setOnCompletionListener(this);

        mp = MediaPlayer.create(this, R.raw.schweinquieken);

        //TESTS
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i,100);
            }
        });

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //teamList = teamSaver.loadTeams("teams");
                //adapterTeamList.notifyDataSetChanged();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //teamSaver.saveTeams("teams", teamList);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if(!isStarted){
                        currentSong = i;
                    }else{
                        playSong(musicList[i]);
                        isStarted = true;
                        play.setBackgroundResource(R.drawable.ic_pause);
                        currentSong = i;

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
                    if(isStarted){
                        if(mediaPlayerMusic.isPlaying()) {
                            mediaPlayerMusic.pause();
                            play.setBackgroundResource(R.drawable.ic_play);

                        }
                        else{
                            mediaPlayerMusic.start();
                            play.setBackgroundResource(R.drawable.ic_pause);
                        }
                    }else{
                        try {
                            playSong(musicList[currentSong]);
                            isStarted = true;
                            spinner.setSelection(currentSong);
                            play.setBackgroundResource(R.drawable.ic_pause);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStarted){

                }else{
                    if(currentSong == 0) {
                        mediaPlayerMusic.reset();
                        currentSong = musicList.length-1;
                        try {
                            playSong(musicList[currentSong]);
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        currentSong--;
                        try {
                            playSong(musicList[currentSong]);
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    play.setBackgroundResource(R.drawable.ic_pause);
                }

            }

        });

        forw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStarted){

                }else{
                    if(currentSong < (musicList.length -1)) {
                        mediaPlayerMusic.reset();
                        currentSong++;
                        try {
                            playSong(musicList[currentSong]);
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        currentSong = 0;
                        try {
                            playSong(musicList[currentSong]);
                            spinner.setSelection(currentSong);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    play.setBackgroundResource(R.drawable.ic_pause);
                }


            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShuffle){
                    isShuffle = false;
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle);
                }
                else{
                    isShuffle = true;
                    shuffle.setBackgroundResource(R.drawable.ic_shuffle_pressed);

                }

                Log.d("HELLO", String.valueOf(isShuffle));
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                try {
                    playSong(musicList[arg2]);
                    currentSong = arg2;
                    play.setBackgroundResource(R.drawable.ic_pause);
                    isStarted = true;
                    spinner.setSelection(currentSong);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        listViewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Object o = listViewTeams.getItemAtPosition(position);
                Button buttonChangeName = (Button) dialogTeamChoice.findViewById(R.id.buttonChangeName);
                Button buttonDeleteTeam = (Button) dialogTeamChoice.findViewById(R.id.buttonDelete);
                Button buttonNewTeam = (Button) dialogTeamChoice.findViewById(R.id.buttonNewTeam);
                blizeldiewinzel();
                buttonChangeName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChangeName.setTitle("Teamname ändern");
                        Button buttonOK = (Button) dialogChangeName.findViewById(R.id.button3);
                        final EditText editName = (EditText) dialogChangeName.findViewById(R.id.editText2);
                        dialogChangeName.setCanceledOnTouchOutside(true);
                        dialogChangeName.show();
                        buttonOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String tmp = editName.getText().toString();
                                if(editName.getText().toString().equals(""))
                                    teamList.get(position).setName("no name selected");
                                else
                                    teamList.get(position).setName(tmp);
                                adapterTeamList.notifyDataSetChanged();
                                editName.setText("");
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
                        Button buttonOK = (Button) dialogNewTeam.findViewById(R.id.button3);
                        final EditText editName = (EditText) dialogNewTeam.findViewById(R.id.editText2);
                        dialogNewTeam.show();
                        imageViewPigClosedEyes.setImageResource(R.drawable.schwein);
                        buttonOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Teams team = new Teams();
                                String tmp = editName.getText().toString();
                                if(editName.getText().toString().equals(""))
                                    team.setName("no name selected");
                                else
                                    team.setName(tmp);
                                team.setDrunk(0);
                                teamList.add(0,team);
                                adapterTeamList.notifyDataSetChanged();
                                editName.setText("");
                                dialogNewTeam.dismiss();
                            }
                        });
                    }
                });


                dialogTeamChoice.show();

            }
        });

        setupDateFormat();
        showTimeAndDate();
        alarmTeam();

    }


    //TEST


    private void blizeldiewinzel(){
        hBlinzeln.postDelayed(new Runnable() {
            int zaehler = 0;
            @Override
            public void run() {
                Random random = new Random();
                delay2 = random.nextInt(4500 - 2500) + 2500;

                if(imageViewPigClosedEyes.getVisibility() == View.VISIBLE)
                    imageViewPigClosedEyes.setVisibility(View.INVISIBLE);
                else {
                    imageViewPigClosedEyes.setVisibility(View.VISIBLE);
                    delay2 = 160;
                }



               hBlinzeln.postDelayed(this, delay2);

            }
        }, delay2);
    }

    private void alarmTeam(){
        final int delay3 = 10000;

        hAlert.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                final int alert = rand.nextInt(teamList.size());
                toggleAlert(alert);

                hAlert.postDelayed(this, delay3);

            }
        },delay3);
    }

    @Override
    public void onCompletion(MediaPlayer player) {

        // check for repeat is ON or OFF
        //if(isRepeat){
        // repeat is on play same song again
        //    playSong(currentSongIndex);
        // } else i
        if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSong = rand.nextInt((musicList.length - 1) - 0 + 1) + 0;
            try {
                playSong(musicList[currentSong]);
                spinner.setSelection(currentSong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSong < (musicList.length - 1)) {
                try {
                    playSong(musicList[currentSong + 1]);
                    spinner.setSelection(currentSong);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentSong += 1;
            } else {
                // play first song
                try {
                    playSong(musicList[0]);
                    spinner.setSelection(currentSong);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentSong = 0;
            }

        }
    }


    private void playSong(String path) throws IllegalArgumentException,
            IllegalStateException, IOException {
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString() + FOLDER; //add var folder to run on actual device!

        path = extStorageDirectory + File.separator + path;

        mediaPlayerMusic.reset();
        mediaPlayerMusic.setDataSource(path);
        mediaPlayerMusic.prepare();
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

                hTimeDate.postDelayed(this, delay);
            }
        }, delay);
    }

    protected void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void initHandlers(){
        hTimeDate = new Handler();
        hMusic = new Handler();
    }

    protected void setupDateFormat() {
        TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(tz);
    }


    private void toggleAlert(int posList) {

        if(teamList.get(posList).getAlerted()){
            teamList.get(posList).setAlerted(false);
        }else{
            teamList.get(posList).setAlerted(true);
            mp.start();
            Collections.sort(teamList, Teams.teamsComparator);


        }

        adapterTeamList.notifyDataSetChanged();
    }

    private ArrayList getListData() {

        Teams team1 = new Teams();
        team1.setName("Error 404");
        team1.setDrunk(5);
        teamList.add(team1);

        Teams team2 = new Teams();
        team2.setName("Grünviehs");
        team2.setDrunk(23);
        teamList.add(team2);

        Teams team3 = new Teams();
        team3.setName("Gagipenners");
        team3.setDrunk(-3);
        teamList.add(team3);

        Teams team4 = new Teams();
        team4.setName("Digge");
        team4.setDrunk(33);
        teamList.add(team4);

        Teams team5 = new Teams();
        team5.setName("Babbisch Guzel");
        team5.setDrunk(42);
        teamList.add(team5);

        Teams team6 = new Teams();
        team6.setName("Spencer Hill");
        team6.setDrunk(11);
        teamList.add(team6);
        // Add some more dummy data for testing
        return teamList;
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

    private Runnable mUpdateTimeTask = new Runnable() {
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

    public void updateSeekBar(){
        hMusic.postDelayed(mUpdateTimeTask, 100);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public String[] getMusic() {
        //FOR ACTUAL DEVICE
       String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgsMp3 = new String[]{  mimeType};
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DISPLAY_NAME }, selectionMimeType , selectionArgsMp3,
                //"LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
                MediaStore.Audio.Media._ID);

        int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(0);
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return songs;
    }
}

