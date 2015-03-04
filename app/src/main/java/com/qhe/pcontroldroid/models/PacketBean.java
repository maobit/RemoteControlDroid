package com.qhe.pcontroldroid.models;

/**
 * Created by sunshine on 15-3-4.
 */

import java.io.Serializable;

public class PacketBean implements Serializable {

    private String packetType;
    private Object data;

    public String getPacketType() {
        return packetType;
    }
    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

}
