package kevin.android.texts.Ending;

public class Ending {
    private String title, description, guide;

    public Ending(String title, String description, String guide) {
        this.title = title;
        this.description = description;
        this.guide = guide;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }
}
