package com.example.guilherme.tcc_1_4.Controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guilherme.tcc_1_4.Model.Data;
import com.example.guilherme.tcc_1_4.Model.Posicao;
import com.example.guilherme.tcc_1_4.Model.Robo;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private SQLiteDatabase database;

    public Database(Context context){
        DatabaseCore auxDB = new DatabaseCore(context);
        database = auxDB.getWritableDatabase();
    }

    public void insert(Robo robo){
        ContentValues values = new ContentValues();
        values.put("nomeRobo", robo.getNomeRobo());
        values.put("enderecoMAC", robo.getEnderecoMAC());
        values.put("tipoRobo", robo.getTipoRobo());
        values.put("statusRobo", robo.getStatusRobo());
        values.put("coordXRobo", robo.getPosicao().getCoordX());
        values.put("coordYRobo", robo.getPosicao().getCoordY());
        values.put("anguloTetaRobo", robo.getPosicao().getAnguloTeta());
        values.put("data", robo.getPosicao().getData().getData());
        values.put("hora", robo.getPosicao().getData().getHora());
        database.insert("robo", null, values);
    }

    public void update(Robo robo){
        ContentValues values = new ContentValues();
        values.put("nomeRobo", robo.getNomeRobo());
        values.put("enderecoMAC", robo.getEnderecoMAC());
        values.put("tipoRobo", robo.getTipoRobo());
        values.put("statusRobo", robo.getStatusRobo());
        values.put("coordXRobo", robo.getPosicao().getCoordX());
        values.put("coordYRobo", robo.getPosicao().getCoordY());
        values.put("anguloTetaRobo", robo.getPosicao().getAnguloTeta());
        values.put("data", robo.getPosicao().getData().getData());
        values.put("hora", robo.getPosicao().getData().getHora());
        database.update("robo", values, "nomeRobo = ?", new String[]{""+robo.getNomeRobo()});
    }

    public void delete(Robo robo){
        database.delete("robo", "nomeRobo = ?", null);
    }

    public List<Robo> getList(){
        List<Robo> listRobo = new ArrayList<Robo>();
        String[] columns = new String[]{"_id", "nomeRobo", "enderecoMACRobo", "tipoRobo", "statusRobo", "coordXRobo", "coordYRobo", "anguloTetaRobo", "data", "hora"};

        Cursor cursor = database.query("robo", columns, null, null, null, null, "nomeRobo ASC");

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Robo robo = new Robo();
                robo.setIdRobo(cursor.getLong(0));
                robo.setNomeRobo(cursor.getString(1));
                robo.setEnderecoMAC(cursor.getString(2));
                robo.setTipoRobo(cursor.getInt(3));
                robo.setStatusRobo(cursor.getInt(4));

                float coordX = cursor.getFloat(5);
                float coordY = cursor.getFloat(6);
                float teta = cursor.getFloat(7);
                String date = cursor.getString(8);
                String hour = cursor.getString(9);

                Data data = new Data(date, hour);
                Posicao posicao = new Posicao(coordX, coordY, teta, data);
                robo.setPosicao(posicao);
                listRobo.add(robo);
            }while(cursor.moveToNext());
        }
        return listRobo;
    }
}
