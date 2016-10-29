package voxspell.settings;

/**
 * The different types of Main Menu backgrounds supported.
 *
 * @author Will Molloy
 */
public enum MainMenuBackground {
    AUTUMN("autumn", "Autumn"),
    SUMMER("summer", "Summer"),
    SPRING("spring", "Spring"),
    WINTER("winter", "Winter");

    private final String id;
    private final String display;

    MainMenuBackground(String id, String display) {
        this.id = id;
        this.display = display;
    }

    /**
     * Returns the .css id associated with the background.
     */
    public String getCssId() {
        return id;
    }

    /**
     * Returns the display text with the associated background.
     */
    public String getDisplay() {
        return display;
    }
}
