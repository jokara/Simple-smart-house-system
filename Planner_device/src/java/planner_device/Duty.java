/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner_device;

/**
 *
 * @author jn160139
 */
public class Duty {
    private String duty;
    private String destionation;
    private int hour;
    private int minute;
    private int second;

    public Duty(String duty, int hour, int minute, int second) {
        this.duty = duty;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.destionation=null;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getDestionation() {
        return destionation;
    }

    public void setDestionation(String destionation) {
        this.destionation = destionation;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
    
}
