package br.com.bitmine.sttool.fragments;

/**
 * This class represents a base fragment.
 */
public interface BackKeyHandleFragment {

    /**
     * Called when the user press Back key.
     * @return - True (if press was handled) or False (otherwise).
     */
    boolean onBackPressed();
}
