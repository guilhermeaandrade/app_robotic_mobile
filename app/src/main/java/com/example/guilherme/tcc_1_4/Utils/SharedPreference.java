package com.example.guilherme.tcc_1_4.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.guilherme.tcc_1_4.Model.Position;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private Context mContext;

    public SharedPreference(Context context){
        super();
        mContext = context;
    }

    public synchronized void storeMoviments(List moviments){
        sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().commit();
        Gson gson = new Gson();
        String jsonMoviments = gson.toJson(moviments);
        editor.putString(Constants.MOVIMENTS_KEY, jsonMoviments);
        editor.commit();
    }

    public synchronized ArrayList<Position> loadMoviments(){
        List<Position> moviments;
        Log.i(Constants.TAG, "ENTREI EM LOADMOVIMENTS");
        sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.MOVIMENTS_KEY)) {
            String jsonMoviments = sharedPreferences.getString(Constants.MOVIMENTS_KEY, null);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Position>>() {}.getType();
            moviments = gson.fromJson(jsonMoviments, type);
        }else {
            return null;
        }
        return (ArrayList<Position>) moviments;
    }
}
