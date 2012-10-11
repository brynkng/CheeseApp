package com.cheeseapp;

public class TimerValue {
    private int time;
    private String unit;

    public TimerValue(int time, String unit) {
        this.time = time;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public int getTime() {
        return time;
    }
}
