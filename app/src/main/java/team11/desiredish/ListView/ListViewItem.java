package team11.desiredish.ListView;

import android.graphics.drawable.Drawable;

/**
 * Created by Yanjing on 10/22/14.
 */
public class ListViewItem {
    public final Drawable image;       // the drawable for the ListView item ImageView
    public final String restName;        // the text for the ListView item title
    public final String description;  // the text for the ListView item description
    public final String time;
    public final String rating;
    public final String restID;
    public final String uploadID;
    public final String friendName;

    public ListViewItem(Drawable icon, String title, String description,String Time,String rating,String RestID,String uploadID,String friendName) {
        this.image = icon;
        this.restName = title;
        this.description = description;
        this.time=Time;
        this.rating=rating;
        this.restID=RestID;
        this.uploadID=uploadID;
        this.friendName=friendName;
    }
}