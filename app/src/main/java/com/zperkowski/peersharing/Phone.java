package com.zperkowski.peersharing;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zperkowski on 22/01/2018.
 */

public class Phone {
    String name;
    InetAddress address;

    public Phone(InetAddress address) {
        this.address = address;
        this.name = "Name";
    }

    public Phone(String address) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.name = "Name";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}
