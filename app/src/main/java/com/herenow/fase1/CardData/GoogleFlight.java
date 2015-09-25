package com.herenow.fase1.CardData;

import org.jsoup.select.Elements;

/**
 * Created by Milenko on 23/09/2015.
 */
public class GoogleFlight {
    private final FlightData mFlight;
    public String hourEstimated, terminal, gate, arrivalExpected, arrivalTerminal, arrivalGate, code;
    public String changesText, changeSummarized;

    public GoogleFlight(Elements datos, FlightData flight) {
        mFlight = flight;

        hourEstimated = datos.first().text();
        terminal = datos.get(1).text();
        gate = datos.get(2).text();
        arrivalExpected = datos.get(3).text();
        arrivalTerminal = datos.get(4).text();
        arrivalGate = datos.get(5).text();
    }

    @Override
    public String toString() {
        return "GoogleFlight{" +
                "hEst='" + hourEstimated + '\'' +
                ", T='" + terminal + '\'' +
                ", Gate='" + gate + '\'' +
                ", ahExp='" + arrivalExpected + '\'' +
                ", aT='" + arrivalTerminal + '\'' +
                ", aGate='" + arrivalGate + '\'' +
                '}';
    }

    public boolean hasChanged(GoogleFlight OldGoogle) {
        StringBuilder sb = new StringBuilder();

        boolean bEstimated = hourEstimated.equals(OldGoogle.hourEstimated);
        boolean bGate = gate.equals(OldGoogle.gate);
        boolean bTerminal = terminal.equals(OldGoogle.gate);

        if (bGate) {
            sb.append(mFlight.toString() + "\n");
            sb.append("Gate: " + OldGoogle.gate + " -> " + gate + " | ");
            if (OldGoogle.equals("-")) {//Check
                changeSummarized = "The gate for the flight " + mFlight.code + "  has been assigned: " + gate;
            } else {
                changeSummarized = "The gate for the flight " + mFlight.code + " has been changed. Now is: " + gate;
            }
        }
        if (bEstimated) {
            sb.append("Estimated: " + OldGoogle.hourEstimated + " -> " + hourEstimated + " | ");
            changeSummarized = "The estimated departure of the flight " + mFlight.code + " is now at: " + gate;
        }
        if (bTerminal) {
            sb.append("Terminal: " + OldGoogle.terminal + " -> " + terminal + " | ");
            changeSummarized = "Change in departure terminal for the flight " + mFlight.code + " . Now is: " + gate;//TODO put destination and codes
        }

        changesText = sb.toString();

        return bEstimated & bGate & bTerminal;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        String status = "on time";//TODO on time, slight delay, delayed,

        sb.append("The flight " + mFlight.code + " with destination " + mFlight.destination + " is " + status + ".\nThe expected departure is at " + hourEstimated);
        if (gate.equals("-")) {
            sb.append(".\nNo gate assigned yet");
        } else {
            sb.append(" at gate " + gate + ".");
        }

        return sb.toString();
    }
}
