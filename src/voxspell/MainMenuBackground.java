package voxspell;

/**
 * The different types of Main Menu backgrounds supported.
 *
 * @author Will Molloy
 */
public enum MainMenuBackground {
    Autumn("autumn"),
    Summer("summer"),
    Spring("spring"),
    Winter("winter");

    private String id;

    MainMenuBackground(String id){
        this.id = id;
    }

    /**
     * Returns the .css id associated with the background.
     */
    public String getId() {
        return id;
    }
}
