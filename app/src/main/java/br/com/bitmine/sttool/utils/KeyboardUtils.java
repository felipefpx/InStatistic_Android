package br.com.bitmine.sttool.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by felipepx on 2/19/16.
 */
public class KeyboardUtils {

    /**
     * Allows to hide/show the soft keyboard.
     * @param activity - Current activity.
     * @param show - True (to show) or false (to hide) the keyboard.
     */
    public static void showKeyboard(Activity activity, boolean show){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && !show){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }
}
