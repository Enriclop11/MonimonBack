package com.enriclop.kpopbot.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "watchtime")
@Data
@Getter
public class WatchTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double minutes;

    private double hours;

    private double days;

    public WatchTime() {
        this.minutes = 0;
        this.hours = 0;
        this.days = 0;
    }

    public void addMinutes(double minutes) {
        this.minutes += minutes;

        if (this.minutes >= 60) {
            this.minutes -= 60;
            this.hours += 1;
        }
    }

    public void addHours(double hours) {
        this.hours += hours;

        if (this.hours >= 24) {
            this.hours -= 24;
            this.days += 1;
        }
    }

    public void addDays(double days) {
        this.days += days;
    }

    @Override
    public String toString() {
        return String.format("%.0f", this.days) + "d " + String.format("%.0f", this.hours) + "h " + String.format("%.0f", this.minutes) + "m";
    }
}
