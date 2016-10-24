package voxspell.tools;

import java.text.DecimalFormat;

/**
 * Contains various methods for formatting numbers e.g. time, accuracy ..
 *
 * @author Will Molloy
 */
public class NumberFormatter {

    public String formatTime(int seconds){
        int mins = seconds / 60;
        seconds = (seconds % 60);
        String formatMins;
        if (mins == 0) {
            formatMins = "00";
        } else if (mins < 10) {
            formatMins = "0" + mins;
        } else {
            formatMins = mins + "";
        }
        String formatSecs = seconds < 10 ? "0" + seconds : seconds + "";

        return formatMins + ":" + formatSecs;
    }

    public String formatAccuracy(double accuracy){
       return new DecimalFormat("####0.00").format(accuracy) + "%";
    }

}
