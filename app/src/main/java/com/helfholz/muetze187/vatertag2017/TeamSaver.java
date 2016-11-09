package com.helfholz.muetze187.vatertag2017;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Muetze187 on 09.07.2016.
 */
public class TeamSaver {
    public static final String PREFS_NAME = "NKDROID_APP";
    public static final String TEAMS = "Teams";

    public TeamSaver(){
       super();

    }

    public void saveTeams(Context context, List teams){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(teams);
        editor.putString(TEAMS, jsonFavorites);
        editor.commit();
    }

    public ArrayList<Teams> loadTeams(Context context)
    {
        SharedPreferences settings;
        List favorites;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(TEAMS)) {
            String jsonFavorites = settings.getString(TEAMS, null);
            Gson gson = new Gson();
           Teams favoriteItems = gson.fromJson(jsonFavorites,Teams.class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList(favorites);
        } else
            return null;
        return (ArrayList) favorites;
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
