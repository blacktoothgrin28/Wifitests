package com.herenow.fase1.CardData;

import com.herenow.fase1.Cards.AirportCard;

import org.jsoup.select.Elements;

import util.myLog;

/**
 * Created by Milenko on 23/09/2015.
 */
public class GoogleFlight {
    private final FlightData mFlight;
    public String hourEstimated, terminal, gate, arrivalExpected, arrivalTerminal, arrivalGate, code;
    public String changesText, changeSummarized;
    private String from;


    public GoogleFlight(Elements datos, FlightData flight) {


        mFlight = flight;


//        Elements datos= datosOri.get(2).child(0).child(2).children();

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

    public String getSummary(AirportCard.TypeOfCard mTypeOfCard) {
        StringBuilder sb = new StringBuilder();
        String status = "on time";//TODO on time, slight delay, delayed,

        try {
            sb.append("The flight " + mFlight.code);

            if (mTypeOfCard == AirportCard.TypeOfCard.departure) {
                sb.append(" with destination " + mFlight.destination + " is " + status + ".\nThe expected departure is at " + hourEstimated);
                if (gate.equals("-")) {
                    sb.append(".\nNo gate assigned yet");
                } else {
                    sb.append(" at gate " + gate + ".");
                }
            } else {
                sb.append(" coming from " + mFlight.destination + " is " + status + ".\nThe expected landing is at " + arrivalExpected); //improve
            }
        } catch (Exception e) {
            myLog.add("No flight summary available:" + e.getLocalizedMessage());
        }

        return sb.toString();
    }
}
