package com.edison.bigdatakafka.entity;

public class PassCar {
    /**过车时间 milli*/
    private long passTime;
    /**机动车号牌信息 如 渝AQ9C52*/
    private String plateNum;
    /**过车卡口信息*/
    private String deviceId;

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
