package util;

/**
 * Created by Milenko on 04/06/2015.
 */


public class WeaconItem {
    private String url;
    private String message;
    private String title;
    private String thumbnail;
//    private String imagePath;

    public WeaconItem(String title, String Message, String thumbnail, String url) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.message = Message;
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


}