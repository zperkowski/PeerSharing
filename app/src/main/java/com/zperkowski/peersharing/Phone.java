package com.zperkowski.peersharing;

/**
 * Created by zperkowski on 22/01/2018.
 */

public class Phone {
    String name;
    String ip;

    public Phone(String ip) {
        this.ip = ip;
        this.name = ip.substring(0, 3);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
