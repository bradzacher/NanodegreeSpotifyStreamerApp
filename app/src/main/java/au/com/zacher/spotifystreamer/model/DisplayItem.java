package au.com.zacher.spotifystreamer.model;

/**
 * Created by Brad on 23/06/2015.
 */
public class DisplayItem {
    public String id;
    public String imageUrl;
    public String title;
    public String subtitle;

    public DisplayItem(String id, String imageUrl, String title) {
        this(id, imageUrl, title, null);
    }
    public DisplayItem(String id, String imageUrl, String title, String subtitle) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
    }
}
