package com.example.guilherme.tcc_1_4.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseCore extends SQLiteOpenHelper {

    private static final String NOME_BD = "tcc";
    private static final int VERSAO_BD = 1;

    public DatabaseCore(Context context){
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table robo(_id integer primary key autoincrement, " +
                "nomeRobo text not null, " +
                "enderecoMACRobo text not null, " +
                "tipoRobo integer not null, " +
                "statusRobo integer not null, " +
                "coordXRobo real not null, " +
                "coordYRobo real not null, " +
                "anguloTetaRobo real not null, " +
                "data text not null, " +
                "hora text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("drop table robo");
        onCreate(database);
    }
}