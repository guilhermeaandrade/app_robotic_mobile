package com.example.guilherme.tcc_1_4.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tcc";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ROBO = "robo";
    private static final String TABLE_POSITION = "posicao";

    private static final String ROBO_COLUMN_ID = "_id";
    private static final String ROBO_COLUMN_POSITION = "id_position";
    private static final String ROBO_COLUMN_NAME = "name";
    private static final String ROBO_COLUMN_ENDERECO = "mac_address";
    private static final String ROBO_COLUMN_TYPE = "type";
    private static final String ROBO_COLUMN_STATUS = "status";

    private static final String POSITION_COLUMN_ID = "_id";
    private static final String POSITION_COLUMN_COORDX = "coordX";
    private static final String POSITION_COLUMN_COORDY = "coordY";
    private static final String POSITION_COLUMN_THETA = "theta";
    private static final String POSITION_COLUMN_DATE = "date";
    private static final String POSITION_COLUMN_HOUR ="hour";

    public DatabaseCore(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("create table " + TABLE_ROBO + "( " +
                            ROBO_COLUMN_ID + "integer primary key autoincrement, " +
                            ROBO_COLUMN_POSITION + " integer, " +
                            ROBO_COLUMN_NAME + " text not null, " +
                            ROBO_COLUMN_ENDERECO +" text not null, " +
                            ROBO_COLUMN_TYPE + " integer not null, " +
                            ROBO_COLUMN_STATUS + " integer not null, " +
                            "foreign key(" + ROBO_COLUMN_POSITION + ") references " + TABLE_POSITION + " ( " + POSITION_COLUMN_ID + "));"
        );

        database.execSQL("create table "+ TABLE_POSITION + "( "+
                            POSITION_COLUMN_ID + "integer primary key autoincrement, " +
                            POSITION_COLUMN_COORDX + "real not null, " +
                            POSITION_COLUMN_COORDY + "real not null, " +
                            POSITION_COLUMN_THETA + "real not null, " +
                            POSITION_COLUMN_DATE + "text not null, " +
                            POSITION_COLUMN_HOUR + "text not null);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBO);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION);
        onCreate(database);
    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public String getTableRobo() {
        return TABLE_ROBO;
    }

    public String getTablePosition() {
        return TABLE_POSITION;
    }

    public String getRoboColumnId() {
        return ROBO_COLUMN_ID;
    }

    public String getRoboColumnPosition() {
        return ROBO_COLUMN_POSITION;
    }

    public String getRoboColumnName() {
        return ROBO_COLUMN_NAME;
    }

    public String getRoboColumnEndereco() {
        return ROBO_COLUMN_ENDERECO;
    }

    public String getRoboColumnType() {
        return ROBO_COLUMN_TYPE;
    }

    public String getRoboColumnStatus() {
        return ROBO_COLUMN_STATUS;
    }

    public String getPositionColumnId() {
        return POSITION_COLUMN_ID;
    }

    public String getPositionColumnCoordx() {
        return POSITION_COLUMN_COORDX;
    }

    public String getPositionColumnCoordy() {
        return POSITION_COLUMN_COORDY;
    }

    public String getPositionColumnTheta() {
        return POSITION_COLUMN_THETA;
    }

    public String getPositionColumnDate() {
        return POSITION_COLUMN_DATE;
    }

    public String getPositionColumnHour() {
        return POSITION_COLUMN_HOUR;
    }
}