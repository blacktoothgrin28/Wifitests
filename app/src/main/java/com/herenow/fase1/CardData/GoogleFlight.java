package com.herenow.fase1.CardData;

import org.jsoup.select.Elements;

/**
 * Created by Milenko on 23/09/2015.
 */
public class GoogleFlight {
    public String hourEstimated, terminal, gate, arrivalExpected, arrivalTerminal, arrivalGate;
    public String hourEstimatedOld, terminalOld, gateOld, arrivalExpectedOld, arrivalTerminalOld, arrivalGateOld;
    public String code;
//    GoogleFlight oldState;

    public GoogleFlight(Elements datos) {
        extractData(datos);

//        oldState = this;
    }

    @Override
    public String toString() {
        return "GoogleFlight{" +
                "hourEstimated='" + hourEstimated + '\'' +
                ", terminal='" + terminal + '\'' +
                ", gate='" + gate + '\'' +
                ", arrivalExpected='" + arrivalExpected + '\'' +
                ", arrivalTerminal='" + arrivalTerminal + '\'' +
                ", arrivalGate='" + arrivalGate + '\'' +
                ", hourEstimatedOld='" + hourEstimatedOld + '\'' +
                ", terminalOld='" + terminalOld + '\'' +
                ", gateOld='" + gateOld + '\'' +
                ", arrivalExpectedOld='" + arrivalExpectedOld + '\'' +
                ", arrivalTerminalOld='" + arrivalTerminalOld + '\'' +
                ", arrivalGateOld='" + arrivalGateOld + '\'' +
                '}';
    }

    private void extractData(Elements datos) {
        hourEstimated = datos.first().text();
        terminal = datos.get(1).text();
        gate = datos.get(2).text();
        arrivalExpected = datos.get(3).text();
        arrivalTerminal = datos.get(4).text();
        arrivalGate = datos.get(5).text();
    }

    public void update(Elements datos) {
        toBuffer();
        extractData(datos);
        compare();
    }

    private boolean compare() {
        boolean changed = false;
        String changedField = "";

        if (!hourEstimatedOld.equals(hourEstimated)) {
            changed = true;
            changedField = "hour";
        }
        if (!terminalOld.equals(terminal)) {
            changedField = "Terminal";
            changed = true;

        }
        if (!gateOld.equals(gate)) {
            changedField = "Gate";

            changed = true;
        }
//        if (!arrivalExpectedOld.equals(arrivalExpected)) {
//            changedField="Expected arrival";
//            changed = true;
//        }

        if (!arrivalTerminalOld.equals(arrivalTerminal)) {
            changedField = "Arrival Terminal";
            changed = true;

        }
//        if (!arrivalGateOld.equals(arrivalGate)) {
//            changedField="Arrival Gate";
//            changed = true;
//
//        }
        return changed;
    }

    private void toBuffer() {
        hourEstimatedOld = hourEstimated;
        terminalOld = terminal;
        gateOld = gate;
        arrivalExpectedOld = arrivalExpected;
        arrivalTerminalOld = arrivalTerminal;
        arrivalGateOld = arrivalGate;
    }

}
