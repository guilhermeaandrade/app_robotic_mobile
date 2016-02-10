package com.example.guilherme.tcc_1_4.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Data {

    private SimpleDateFormat dateFormater;
    private static final String dateTemplate = "dd/MM/yyyy";
    private static final String timeTemplate = "hh:mm:ss";
    private String data;
    private String hora;
    private Calendar calendar;
    private int day, month, year, hour, min, sec;

    public Data(String data, String hora) {
        this.data = data;
        this.hora = hora;
    }

    public Data() {
        calendar = Calendar.getInstance(Locale.getDefault());
        dateFormater = new SimpleDateFormat(dateTemplate);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

}
