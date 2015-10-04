package com.herenow.fase1.CardData;

import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.FlightData;

import org.jsoup.select.Elements;

import util.myLog;

/**
 * Created by Milenko on 23/09/2015.
 */
public class GoogleFlight {
    //    private final FlightData mFlight;
    private final boolean dataAvailable;
    public String remoteCity;
    public String hourEstimated, terminal, gate, arrivalExpected, arrivalTerminal, arrivalGate, code;
    public String changesText, changeSummarized;


    public GoogleFlight(Elements datos) {
        dataAvailable = (datos.size() > 0);

        if (dataAvailable) {

            hourEstimated = datos.first().text();
            terminal = datos.get(1).text();
            gate = datos.get(2).text();
            arrivalExpected = datos.get(3).text();
            arrivalTerminal = datos.get(4).text();
            arrivalGate = datos.get(5).text();
        }
    }


    public void setCode(String flightCode) {
        this.code = flightCode;
    }


    public void setRemoteCity(String remoteCity) {
        this.remoteCity = remoteCity;
    }

    public GoogleFlight(Elements datos, FlightData flight) {
        this(datos);
        remoteCity = flight.remoteCity;
        code = flight.code;
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

        boolean changeEstimated = !hourEstimated.equals(OldGoogle.hourEstimated);
        boolean changeGate = !gate.equals(OldGoogle.gate);
        boolean changeTerminal = !terminal.equals(OldGoogle.terminal);

        if (changeGate) {
            sb.append(code + "\n");
            sb.append("Gate: " + OldGoogle.gate + " -> " + gate + " | ");
            if (OldGoogle.equals("-")) {//Check
                changeSummarized = "The gate for the flight " + code + "  has been assigned: " + gate;
            } else {
                changeSummarized = "The gate for the flight " + code + " has been changed. Now is: " + gate;
            }
        }
        if (changeEstimated) {
            sb.append("Estimated: " + OldGoogle.hourEstimated + " -> " + hourEstimated + " | ");
            changeSummarized = "The estimated departure of the flight " + code + " is now at: " + gate;
        }
        if (changeTerminal) {
            sb.append("Terminal: " + OldGoogle.terminal + " -> " + terminal + " | ");
            changeSummarized = "Change in departure terminal for the flight " + code + " . Now is: " + terminal;//TODO put destination and codes
        }

        changesText = sb.toString();

        return changeEstimated || changeGate || changeTerminal;
    }

    public String getSummary(AirportCard.TypeOfCard mTypeOfCard) {
        StringBuilder sb = new StringBuilder();
        String status = "on time";//TODO on time, slight delay, delayed,

        if (!dataAvailable) return "No data available for flight " + code;

        try {
            sb.append("The flight " + code);

            if (mTypeOfCard == AirportCard.TypeOfCard.departure) {
                sb.append(" with destination " + remoteCity + " is " + status + ".\nThe expected departure is at " + hourEstimated);
                if (gate.equals("-")) {
                    sb.append(".\nNo gate assigned yet");
                } else {
                    sb.append(" at gate " + gate + ".");
                }
            } else {
                sb.append(" coming from " + remoteCity + " is " + status + ".\nThe expected landing is at " + arrivalExpected); //todo improve
            }
        } catch (Exception e) {
            myLog.add("No flight summary available:" + e.getLocalizedMessage());
        }

        return sb.toString();
    }

    /**
     * Indicates that no more updates are needed
     * @return
     */
    public boolean hasDepartedOrLanded() {
        return false;//TODO complete from Google flight card data
    }
}
