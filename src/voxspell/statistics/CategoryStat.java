package voxspell.statistics;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a category stat:
 * Category stats have a category name, total words spelt (correct+incorrect) and accuracy.
 */
public class CategoryStat implements Comparable<CategoryStat> {

    private SimpleStringProperty category;
    private SimpleStringProperty totalSpelt;
    private SimpleStringProperty accuracy;
    private SimpleStringProperty bestStreak;
    private SimpleStringProperty bestTime;

    public CategoryStat(String category, String totalSpelt, String accuracy, String bestStreak, String bestTime) {
        this.category = new SimpleStringProperty(category);
        this.totalSpelt = new SimpleStringProperty(totalSpelt);
        this.accuracy = new SimpleStringProperty(accuracy);
        this.bestStreak = new SimpleStringProperty(bestStreak);
        this.bestTime = new SimpleStringProperty(bestTime);
    }

    // Getter and setters for observable array list

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getTotalSpelt() {
        return totalSpelt.get();
    }

    public void setTotalSpelt(String totalSpelt) {
        this.totalSpelt.set(totalSpelt);
    }

    public String getAccuracy() {
        return accuracy.get();
    }

    public void setAccuracy(String accuracy) {
        this.accuracy.set(accuracy);
    }

    public String getBestStreak() {
        return bestStreak.get();
    }

    public void setBestStreak(String bestStreak) {
        this.bestStreak.set(bestStreak);
    }

    public String getBestTime() {
        return bestTime.get();
    }

    public void setBestTime(String bestTime) {
        this.bestTime.set(bestTime);
    }

    /**
     * Sort by total words spelt
     */
    @Override
    public int compareTo(CategoryStat o) {
        return Integer.parseInt(o.totalSpelt.get()) - Integer.parseInt(totalSpelt.get());
    }
}
