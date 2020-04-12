package model;

public class SettingsItem {
    private final int ID;
    private final String title;
    private final String description;
    private final boolean value;

    public SettingsItem(int ID, String title, String description, boolean value) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.value = value;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean getValue() {
        return value;
    }
}
