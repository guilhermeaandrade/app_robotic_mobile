package com.example.guilherme.tcc_1_4.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guilherme.tcc_1_4.Model.Data;
import com.example.guilherme.tcc_1_4.Model.Posicao;
import com.example.guilherme.tcc_1_4.Model.Robo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private SQLiteDatabase database;
    private String[] allColumnsTableRobo;
    private String[] allColumnsTablePosition;
    private DatabaseCore databaseCore;

    public Database(Context context){
        databaseCore = new DatabaseCore(context);
        database = databaseCore.getWritableDatabase();

        allColumnsTableRobo = new String[]{
                databaseCore.getRoboColumnId(), databaseCore.getRoboColumnPosition(), databaseCore.getRoboColumnName(),
                databaseCore.getRoboColumnEndereco(), databaseCore.getRoboColumnType(), databaseCore.getRoboColumnStatus()
        };

        allColumnsTablePosition = new String[]{
                databaseCore.getPositionColumnId(), databaseCore.getPositionColumnCoordx(), databaseCore.getPositionColumnCoordy(),
                databaseCore.getPositionColumnTheta(), databaseCore.getPositionColumnDate(), databaseCore.getPositionColumnHour()
        };
    }

    //########################## SQL METHODS TO POSITION ###########################
    private Posicao insertPosition(Posicao pos){
        ContentValues values = new ContentValues();
        values.put(allColumnsTablePosition[1], pos.getCoordX());
        values.put(allColumnsTablePosition[2], pos.getCoordY());
        values.put(allColumnsTablePosition[3], pos.getAnguloTeta());
        values.put(allColumnsTablePosition[4], pos.getData().getData());
        values.put(allColumnsTablePosition[5], pos.getData().getHora());
        long insertedId = database.insert(databaseCore.getTablePosition(), null, values);
        Cursor cursor = database.query(databaseCore.getTablePosition(), allColumnsTablePosition,
                databaseCore.getPositionColumnId() + " = " + insertedId, null, null, null, null);
        cursor.moveToFirst();
        Posicao position = cursorToPosition(cursor);
        cursor.close();
        return position;
    }

    private void deletePosition(Posicao pos){
        long id = pos.getId();
        database.delete(databaseCore.getTablePosition(),
                databaseCore.getPositionColumnId() + " = " + id, null);
    }

    private List<Posicao> getAllPositions(){
        List<Posicao> positions = new ArrayList<Posicao>();
        Cursor cursor = database.query(databaseCore.getTablePosition(), allColumnsTablePosition,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Posicao pos = cursorToPosition(cursor);
            positions.add(pos);
            cursor.moveToNext();
        }
        cursor.close();
        return positions;
    }

    private Posicao updatePosition(Posicao pos){
        ContentValues values = new ContentValues();
        values.put(allColumnsTablePosition[1], pos.getCoordX());
        values.put(allColumnsTablePosition[2], pos.getCoordY());
        values.put(allColumnsTablePosition[3], pos.getAnguloTeta());
        values.put(allColumnsTablePosition[4], pos.getData().getData());
        values.put(allColumnsTablePosition[5], pos.getData().getHora());
        database.update(databaseCore.getTablePosition(), values,
                databaseCore.getPositionColumnId() + " = " + pos.getId(), null);
        Cursor cursor = database.query(databaseCore.getTablePosition(), allColumnsTablePosition,
                databaseCore.getPositionColumnId() + " = " + pos.getId(), null, null, null, null);
        cursor.moveToFirst();
        Posicao position = cursorToPosition(cursor);
        cursor.close();
        return position;
    }

    private Posicao cursorToPosition(Cursor cursor){
        Posicao position = new Posicao();
        position.setId(cursor.getLong(0));
        position.setCoordX(cursor.getFloat(1));
        position.setCoordY(cursor.getFloat(2));
        position.setAnguloTeta(cursor.getFloat(3));

        Data data = new Data();
        data.setData(cursor.getString(4));
        data.setHora(cursor.getString(5));
        position.setData(data);
        return position;
    }

    //########################## SQL METHODS TO POSITION ###########################
    private Robo insertRobo(Robo robo){
        ContentValues values = new ContentValues();
        values.put(allColumnsTableRobo[1], robo.getPosicao().getId());
        values.put(allColumnsTableRobo[2], robo.getNomeRobo());
        values.put(allColumnsTableRobo[3], robo.getEnderecoMAC());
        values.put(allColumnsTableRobo[4], robo.getTipoRobo());
        values.put(allColumnsTableRobo[5], robo.getStatusRobo());
        long insertedId = database.insert(databaseCore.getTableRobo(), null, values);
        Cursor cursor = database.query(databaseCore.getTableRobo(), allColumnsTableRobo,
                databaseCore.getRoboColumnId() + " = " + insertedId, null, null, null, null);
        cursor.moveToFirst();
        Robo rb = cursorToRobo(cursor);
        cursor.close();
        return rb;
    }

    private void deleteRobo(Robo robo){
        long id = robo.getIdRobo();
        database.delete(databaseCore.getTableRobo(),
                databaseCore.getRoboColumnId() + " = " + id, null);
    }

    private List<Robo> getAllRobos(){
        List<Robo> robos = new ArrayList<Robo>();
        Cursor cursor = database.query(databaseCore.getTableRobo(), allColumnsTableRobo,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Robo robo = cursorToRobo(cursor);
            robos.add(robo);
            cursor.moveToNext();
        }
        cursor.close();
        return robos;
    }

    private Robo updateRobo(Robo robo){
        ContentValues values = new ContentValues();
        values.put(allColumnsTableRobo[2], robo.getNomeRobo());
        values.put(allColumnsTableRobo[3], robo.getEnderecoMAC());
        values.put(allColumnsTableRobo[4], robo.getTipoRobo());
        values.put(allColumnsTableRobo[5], robo.getStatusRobo());
        database.update(databaseCore.getTableRobo(), values,
                databaseCore.getRoboColumnId() + " = " + robo.getIdRobo(), null);
        Cursor cursor = database.query(databaseCore.getTableRobo(), allColumnsTableRobo,
                databaseCore.getRoboColumnId() + " = " + robo.getIdRobo(), null, null, null, null);
        cursor.moveToFirst();
        Robo rb = cursorToRobo(cursor);
        cursor.close();
        return rb;
    }

    private Robo cursorToRobo(Cursor cursor){
        Robo robo = new Robo();
        robo.setIdRobo(cursor.getLong(0));
        robo.setPositions(getPositionsOfRobo(robo.getIdRobo()));
        robo.setNomeRobo(cursor.getString(2));
        robo.setEnderecoMAC(cursor.getString(3));
        robo.setTipoRobo(cursor.getInt(4));
        robo.setStatusRobo(cursor.getInt(5));
        return robo;
    }

    private HashMap<Long, Posicao> getPositionsOfRobo(Long id){
        HashMap<Long, Posicao> positions = new HashMap<Long, Posicao>();
        Integer status = 1;
        String sqlQuery = "SELECT * " +
                          " FROM " + databaseCore.getTablePosition() +
                          " WHERE " + databaseCore.getPositionColumnId() + " IN ( SELECT " + databaseCore.getRoboColumnPosition() +
                                                                                " FROM " + databaseCore.getTableRobo() +
                                                                                " WHERE " + databaseCore.getRoboColumnId() + " = " + id +
                                                                                " AND " + databaseCore.getRoboColumnStatus() + " = " + status + " )";
        Cursor c = database.rawQuery(sqlQuery, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            Posicao pos = cursorToPosition(c);
            positions.put(pos.getId(), pos);
        }
        return positions;
    }
}
