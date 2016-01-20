package util;

import android.support.annotation.NonNull;

import com.herenow.fase1.LineTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import parse.WeaconParse;

/**
 * Created by Milenko on 29/10/2015.
 */
public class stringUtils {
    @NonNull
    public static String ConcatenateComma(String[] lista) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lista.length - 1; i++) {
            sb.append(lista[i] + ", ");
        }
        sb.append(lista[lista.length - 1]);

        return sb.toString();
    }

    public static String TrimFirstWord(String s) {
        int i = s.indexOf(" ");
        return s.substring(i + 1);
    }

    public static String TrimFirstWords(String s, int n) {
        String sa = s;
        for (int i = 0; i < n; i++) {
            String sol = TrimFirstWord(sa);
            sa = sol;
        }
        return sa;
    }

    /**
     * reformat the times as
     * L1: 3 min, 4 min, 50 min.
     * L9: 6min, 72 min.
     *
     * @return
     */
    public static String TimesSummarySorted(ArrayList<LineTime> lineTimes) {
        String substring = "No info";

        if (lineTimes.size() > 0) {
            try {
                formatter form=new formatter(lineTimes);
                HashMap<String, ArrayList<LineTime>> tableLines =form.getTable();

                StringBuilder sb = new StringBuilder();
                for (String name : tableLines.keySet()) {
                    sb.append(summaryLineTimes(name, tableLines.get(name)) + "\n");
                }
                String s = sb.toString();
                substring = s.substring(0, s.length() - 2);

            } catch (Exception e) {
                myLog.add("error mostrando resumen ordenado de timeline" + e.getLocalizedMessage());
            }
        }

        return substring;
    }

    public static String summaryLineTimes(String name, ArrayList<LineTime> lineTimes) {
        StringBuilder sb = new StringBuilder(name + ": ");
        for (LineTime lineTime : lineTimes) {
            sb.append(lineTime.roundedTime + ", ");
        }
        String s = sb.toString();
        return (s.substring(0, s.length() - 2) + ".");
    }

    public static ArrayList<String> formatedSummary(ArrayList<LineTime> lineTimes) {
        if (lineTimes.size() == 0) return null;

        ArrayList<String> arr = new ArrayList<>();
        for (LineTime lt : lineTimes) {
//TODO complete with spanformat
            StringBuilder sb = new StringBuilder(lt.lineCode);

        }
        return arr;
    }


    public static String Listar(HashSet<WeaconParse> weaconHashSet) {
        StringBuilder sb = new StringBuilder();
        for (WeaconParse we : weaconHashSet) {
            sb.append(we.getName() + " | ");
        }
        return sb.toString();
    }

    public static String Listar(HashMap<WeaconParse, Integer> contabilidad) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<WeaconParse, Integer> entry : contabilidad.entrySet()) {
            sb.append(entry.getKey().getName() + ":" + entry.getValue() + " | ");
        }
        return sb.toString();
    }
}

