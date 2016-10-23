package voxspell.settings;

/**
 * Different types of Festival voices supported.
 */
public enum Voice {
    US("US English", "(voice_kal_diphone)"),
    UK("UK English", "(voice_rab_diphone)"),
    NZ("NZ English", "(voice_akl_nz_jdt_diphone)");

    private final String display;
    private final String code;

    Voice(String display, String code){
        this.display = display;
        this.code = code;
    }

    /**
     * Returns the display version of the associated voice.
     */
    @Override
    public String toString() {
        return display;
    }

    /**
     * Returns the festival code associated with the voice.
     */
    public String getCode() {
        return code;
    }
}
