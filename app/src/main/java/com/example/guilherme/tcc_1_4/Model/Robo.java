package com.example.guilherme.tcc_1_4.Model;


public class Robo {

    private String name;
    private String enderecoMAC;

    public Robo(String name, String enderecoMAC) {
        this.name = name;
        this.enderecoMAC = enderecoMAC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnderecoMAC() {
        return enderecoMAC;
    }

    public void setEnderecoMAC(String enderecoMAC) {
        this.enderecoMAC = enderecoMAC;
    }
}
