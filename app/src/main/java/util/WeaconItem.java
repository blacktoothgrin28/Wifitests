package util;

/**
 * Created by Milenko on 04/06/2015.
 */


public class WeaconItem {
    private String message;
    private String title;
    private String thumbnail;

    public WeaconItem(String title, String Message, String thumbnail) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.message = Message;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}