package br.com.bitmine.sttool.utils;

import java.util.Formatter;

/**
 * This class allows to change the number format.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class FormatUtils {

    /**
     * Formats a double value to scientific notation.
     * @param value - The value.
     * @return - Scientific notation.
     */
    public static String formatToScientific(double value){
        Formatter fmt = new Formatter();
        fmt.format("%4.2e", value);

        return fmt.toString();
    }
}
