package com.enriclop.kpopbot.utilities;

public class Timer extends Thread{

    private int seconds;
    private int minutes;
    private int hours;
    private Boolean active = true;

    public Timer() {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    public Timer(int seconds, int minutes, int hours) {
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
    }

    public void run() {
        while (active) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            if (minutes == 60) {
                minutes = 0;
                hours++;
            }
        }
    }

    public void stopTimer() {
        active = false;
    }

    public String getTime() {
        return hours + ":" + minutes + ":" + seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours(){
        return hours;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setMinutes(int minutes){
        this.minutes = minutes;
    }

    public void setHours(int hours){
        this.hours = hours;
    }
}
