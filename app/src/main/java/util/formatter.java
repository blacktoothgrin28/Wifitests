package util;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.herenow.fase1.LineTime;

import java.util.ArrayList;
import java.util.HashMap;

public class formatter {
    HashMap<String, ArrayList<LineTime>> tableLines;

    public formatter(ArrayList<LineTime> lineTimes) {
        tableLines = organizeLines(lineTimes);
    }

    /**
     * Create a table  L1 | {lineTimes}
     *
     * @param lineTimes
     * @return
     */
    @NonNull
    private static HashMap<String, ArrayList<LineTime>> organizeLines(ArrayList<LineTime> lineTimes) {
        HashMap<String, ArrayList<LineTime>> tableLines = new HashMap<>();
        ArrayList arr;

        if (lineTimes == null) return null;

        for (LineTime lineTime : lineTimes) {
            String lc = lineTime.lineCode;
            if (tableLines.containsKey(lc)) {
                //add a time to the line
                arr = tableLines.get(lc);
                arr.add(lineTime);
                tableLines.put(lc, arr);
            } else {
                //add a line with first time
                arr = new ArrayList();
                arr.add(lineTime);
                tableLines.put(lc, arr);
            }
        }
        return tableLines;
    }

    /**
     * Shows only the first arrival by line:  L1:10m | B3: 5m | R4:18m
     *
     * @param compact for having L1:10|B3:5|R4:18
     * @return
     */
    public String summarizeAllLines(boolean compact) {
        String substring = "No info";
        int del = 0;

        if (tableLines == null) return "No lines available";

        if (tableLines.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (String name : tableLines.keySet()) {
                String roundedTime = tableLines.get(name).get(0).roundedTime;
                int pos = roundedTime.indexOf(" ");

                if (pos == 0) {//case "IMMINENT"
                    if (compact) {
                        sb.append(name + ":IM|");
                        del = 1;
                    } else {
                        sb.append(name + ": IMM | ");
                        del = 2;
                    }
                    continue;
                }
                if (compact) {
                    sb.append(name + ":" + roundedTime.substring(0, pos) + "|");
                    del = 1;
                } else {
                    sb.append(name + ": " + roundedTime.substring(0, pos) + "m | ");
                    del = 2;
                }
            }
            String s = sb.toString();
            substring = s.substring(0, s.length() - del);
        }

        return substring;
    }

    public String summarizeAllLines() {
        return summarizeAllLines(false);
    }

    /**
     * Array with strings that summarizes each line: L1: 12 min, 18 min, 35 min
     *
     * @return
     */
    public ArrayList<SpannableString> summarizeByOneLine() {
        ArrayList<SpannableString> arr = new ArrayList<>();

        if (tableLines == null || tableLines.keySet().size() == 0) {
            arr.add(new SpannableString("No info for this stop by now."));
        } else {

            for (String name : tableLines.keySet()) {
                ArrayList<LineTime> arrTimes = tableLines.get(name);
                StringBuilder sb = new StringBuilder(name + " ");

                for (LineTime lineTime : arrTimes) {
                    sb.append(lineTime.roundedTime + ", ");
                }

                String s = sb.toString();
                String sub = s.substring(0, s.length() - 2);

                //add format
                SpannableString span = new SpannableString(sub);
                span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(1.1f), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                arr.add(span);
            }
        }
        return arr;
    }

    public HashMap<String, ArrayList<LineTime>> getTable() {
        return tableLines;
    }
}