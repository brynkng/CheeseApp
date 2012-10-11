package com.cheeseapp;

import java.util.ArrayList;

public class TimerValueRetriever {

    public ArrayList<TimerValue> retrieve(String text) {
        String[] unitStrings = {"minute", "hour", "second"};
        int lastIndex = 0;
        ArrayList<TimerValue> timerValues = new ArrayList<TimerValue>();

        while(lastIndex != -1) {
            int closestMatch = -1;
            int closestMatchLength = 0;
            for (String unitString : unitStrings) {
                int matchedIndex = text.indexOf(unitString, lastIndex);
                if ((matchedIndex < closestMatch && matchedIndex != -1) || closestMatch == -1) {
                    closestMatch = matchedIndex;
                    closestMatchLength = unitString.length();
                }
            }
            lastIndex = closestMatch;

            int approxTimeStringLength = 4;

            if (lastIndex < approxTimeStringLength) {
                approxTimeStringLength = lastIndex;
            }

            if( lastIndex != -1){
                String timeString = text.substring(lastIndex - approxTimeStringLength, lastIndex);
                String formattedTimeString = timeString.replaceAll("\\D+", "");
                if (!formattedTimeString.equals("")) {
                    int time = Integer.parseInt(formattedTimeString);
                    String unit = text.substring(lastIndex, lastIndex + closestMatchLength);
                    timerValues.add(new TimerValue(time, unit));
                }

                lastIndex += closestMatchLength;
            }
        }

        return timerValues;
    }
}
