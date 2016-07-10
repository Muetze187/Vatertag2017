package com.helfholz.muetze187.vatertag2017;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends AppCompatActivity {

    private String[] mMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        mMusicList = getMusic();

        ListView lv = (ListView) findViewById(R.id.listView);


        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mMusicList);

        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int songIndex = i;

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                finish();
            }
        });


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
