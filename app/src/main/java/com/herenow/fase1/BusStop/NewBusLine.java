package com.herenow.fase1.BusStop;

import java.util.ArrayList;

/**
 * Created by Milenko on 02/02/2016.
 */
public abstract class NewBusLine {
    protected String lineCode;
    protected ArrayList<NewBus> buses;

    public NewBusLine(String lineCode, NewBusStCg bus) {
        this.lineCode = lineCode;
        buses = new ArrayList<>();
        addBus(bus);
    }

    public NewBusLine() {
    }

    public void addBus(NewBus bus) {
        buses.add(bus);
    }

    public String summary() {
        StringBuilder sb = new StringBuilder(lineCode + " ");
        for (NewBus bus : buses) {
            sb.append(bus.arrivalTimeText + " | ");
        }

        String s = sb.toString();

        return s;
    }

    public int getShortestTime() {
        int min = -1;
        for (NewBus bus : buses) {
            if (bus.arrivalTimeMins < min) min = bus.arrivalTimeMins;
        }
        return min;
    }
}
