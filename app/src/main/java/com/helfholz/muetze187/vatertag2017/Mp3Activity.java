package com.helfholz.muetze187.vatertag2017;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Mp3Activity extends Activity {


    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    ListView listViewPlaylist;
    ArrayAdapter<String> mAdapter;

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

        Intent intent = getIntent();
        String[] mMusicList = intent.getStringArrayExtra("music");

        listViewPlaylist = (ListView) findViewById(R.id.listView2);
        mAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mMusicList);
        listViewPlaylist.setAdapter(mAdapter);

    }

    protected void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
