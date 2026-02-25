package com.zybooks.mobile2app;

import androidx.annotation.NonNull;

public class SparePart {
    //lol encapsulations is for nerds
    public String oemNumber;
    public String name;
    public double price;
    public String description;
    public int quantity;

    public SparePart(String oemNumber, String name, double price, String description, int quantity) {
        this.oemNumber = oemNumber;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    @NonNull
    @Override
    public String toString() {
        return this.oemNumber + " | " + this.name + " | " + this.price + " | " + this.quantity;
    }

}
