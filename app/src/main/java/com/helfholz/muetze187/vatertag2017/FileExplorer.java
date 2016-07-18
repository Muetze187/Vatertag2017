package com.helfholz.muetze187.vatertag2017;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muetze187 on 12.07.2016.
 */
public class FileExplorer extends ListActivity {

    private List<String> item = null;
    private List<String> path = null;
    //private String root="";
    private String root = Environment.getExternalStorageDirectory().toString()+"/Music/" ;
    private TextView myPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fileexplorer);
        myPath = (TextView)findViewById(R.id.path);

        getDir(root);

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
           /* if(dirPath.equals(root)){
                item.add("../");
                path.add(f.getParent());
            }*/

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

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.filexplorer_row, item);
        setListAdapter(fileList);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        File file = new File(path.get(position));

        if (file.isDirectory()) {
            if(file.canRead())
                getDir(path.get(position));
            else
            {
                new AlertDialog.Builder(this)
                        //.setIcon(R.drawable.schwein)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {// TODO Auto-generated method stub
                                    }

                                }).show();
            }
        }
        else {
            new AlertDialog.Builder(this)
                    //.setIcon(R.drawable.schwein)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

        }

    }

}
