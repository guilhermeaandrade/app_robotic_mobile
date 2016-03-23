package com.example.guilherme.tcc_1_4.Model;

import java.util.HashMap;

public class Robo {

    private long idRobo;
    private String nomeRobo;
    private String enderecoMAC;
    private int tipoRobo; //1-Mestre, 2-Escravo
    private int statusRobo; //1-ativo, 2- inativo
    private Posicao posicao;
    private HashMap<Long, Posicao> positions;
    private int photo;

    public Robo(){}

    public Robo(String name, int type, int photo){
        this.nomeRobo = name;
        this.tipoRobo = type;
        this.photo = photo;
        positions = new HashMap<Long, Posicao>();
    }

    public Robo(String nomeRobo, String enderecoMAC, int tipoRobo, int statusRobo, Posicao pos, int p) {
        this.nomeRobo = nomeRobo;
        this.enderecoMAC = enderecoMAC;
        this.tipoRobo = tipoRobo;
        this.statusRobo = statusRobo;
        positions = new HashMap<Long, Posicao>();
        this.posicao = pos;
        insertPosition(this.posicao);
        this.photo = p;
    }

    public Robo(String nomeRobo, String enderecoMAC, int tipoRobo, int statusRobo, int p) {
        this.nomeRobo = nomeRobo;
        this.enderecoMAC = enderecoMAC;
        this.tipoRobo = tipoRobo;
        this.statusRobo = statusRobo;
        positions = new HashMap<Long, Posicao>();
        this.photo = p;
    }

    public void insertPosition(Posicao pos){
        this.positions.put(pos.getId(), pos);
    }

    public HashMap<Long, Posicao> getPositions() {
        return positions;
    }

    public void setPositions(HashMap<Long, Posicao> positions) {
        this.positions = positions;
    }

    public int getPhoto() { return photo; }

    public void setPhoto(int photo) { this.photo = photo; }

    public long getIdRobo() {
        return idRobo;
    }

    public void setIdRobo(long idRobo) {
        this.idRobo = idRobo;
    }

    public String getNomeRobo() {
        return nomeRobo;
    }

    public void setNomeRobo(String nomeRobo) {
        this.nomeRobo = nomeRobo;
    }

    public String getEnderecoMAC() {
        return enderecoMAC;
    }

    public void setEnderecoMAC(String enderecoMAC) {
        this.enderecoMAC = enderecoMAC;
    }

    public int getTipoRobo() {
        return tipoRobo;
    }

    public void setTipoRobo(int tipoRobo) {
        this.tipoRobo = tipoRobo;
    }

    public int getStatusRobo() {
        return statusRobo;
    }

    public void setStatusRobo(int statusRobo) {
        this.statusRobo = statusRobo;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(Posicao posicao) {
        this.posicao = posicao;
    }
}

