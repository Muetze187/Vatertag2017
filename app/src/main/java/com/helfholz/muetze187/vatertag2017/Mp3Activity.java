package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mp3Activity extends Activity {


    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private List<String> item = null;
    private List<String> path = null;
    private String root = Environment.getExternalStorageDirectory().toString()+"/Music/" ;
    private TextView myPath;
    int songIndex = 0;
    ListView listViewPlaylist;
    ArrayAdapter<String> mAdapter;
    MainActivity mainActivity = new MainActivity();
   String fileName = "";

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
                    //Intent i = new Intent(Mp3Activity.this, MainActivity.class);
                    //int songIndex = i;

                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    //in.putExtra("songIndex", songIndex);
                    in.putExtra("filename", fileName);
                    setResult(200, in);
                    finish();
                    //startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        //this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        hideStatusBar();
        //force landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_mp3);

        //Intent intent = getIntent();
        //String[] mMusicList = intent.getStringArrayExtra("music");

        listViewPlaylist = (ListView) findViewById(R.id.listViewFilepath);
       // mAdapter = new ArrayAdapter<String>(this,
         //       R.layout.spinner_item, mMusicList);
        //listViewPlaylist.setAdapter(mAdapter);

        myPath = (TextView)findViewById(R.id.textViewNoData);
        getDir(root);

        listViewPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = new File(path.get(i));

                if (file.isDirectory()) {
                    if(file.canRead())
                        getDir(path.get(i));
                    //else
                   // {
                     /*   new AlertDialog.Builder(getApplicationContext())
                                //.setIcon(R.drawable.schwein)
                                .setTitle("[" + file.getName() + "] folder can't be read!")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {// TODO Auto-generated method stub
                                            }

                                        }).show();*/
                   // }
                }
                else {
                    /*new AlertDialog.Builder(getApplicationContext())
                            //.setIcon(R.drawable.schwein)
                            .setTitle("[" + file.getName() + "]")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();

*/

                try {
                        mainActivity.playSong(file.getAbsolutePath());
                        //mainActivity.playSong(file.getName() +".mp3");
                        //mainActivity.isStarted = true;
                    fileName = file.getName();
                    } catch (IOException e) {
                        e.printStackTrace();
                   }
                   // songIndex = i;
                    //Log.d("currentSong", "depp " + songIndex);
                }
            }
        });

    }

    public String getName(){
        return fileName;
    }

    protected void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void getDir(String dirPath) {
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root)) {
            item.add("zurück");
            path.add(root);
        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];
            path.add(file.getPath());
            if(file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.spinner_item, item);
        listViewPlaylist.setAdapter(fileList);

    }

}
