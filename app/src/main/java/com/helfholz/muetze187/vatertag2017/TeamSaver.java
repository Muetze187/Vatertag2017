package com.helfholz.muetze187.vatertag2017;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Muetze187 on 09.07.2016.
 */
public class TeamSaver {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public TeamSaver(Context context){
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    public void saveTeams(String key, ArrayList<Teams> list){
       Set<Teams> set = new HashSet<Teams>();
        set.addAll(list);
       // editor.remove(key);
        editor.putString(key, String.valueOf(set));
        editor.commit();
    }

    public ArrayList<Teams> loadTeams(String key){
      return null;
    }

    private ArrayList<Teams> getDefaultArray() {
        ArrayList<Teams> array = new ArrayList<Teams>();
        Teams team1 = new Teams();
        team1.setName("Team1");
        Teams team2 = new Teams();
        team2.setName("Team2");
        Teams team3 = new Teams();
        team3.setName("Team3");
        array.add(team1);
        array.add(team2);
        array.add(team3);

        return array;
    }
}
