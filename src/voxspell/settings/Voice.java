package voxspell.settings;

/**
 * Different types of Festival voices supported.
 *
 * @author Will Molloy
 */
public enum Voice {
    US("US English", "(voice_kal_diphone)"),
    UK("UK English", "(voice_rab_diphone)"),
    NZ("NZ English", "(voice_akl_nz_jdt_diphone)");

    private final String display;
    private final String code;

    Voice(String display, String code) {
        this.display = display;
        this.code = code;
    }

    /**
     * Returns the display version of the associated voice.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Returns the festival code associated with the voice.
     */
    public String getScmCode() {
        return code;
    }
}
