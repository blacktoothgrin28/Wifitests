package util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.herenow.fase1.R;

/**
 * Created by Milenko on 04/06/2015.
 */
//public class FeedListRowHolder {
//}
//
//
//import android.content.Intent;
//        import android.support.v7.widget.RecyclerView;
//        import android.view.View;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//        import android.widget.Toast;

public class FeedListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;
    protected TextView title;

    public FeedListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
    }

}