package util;

/**
 * Created by Milenko on 04/06/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import parse.WeaconParse;

public class WeaconAdapter extends RecyclerView.Adapter<WeaconHolder> implements View.OnClickListener {

    private List<WeaconParse> weaconItemList;
    private Context mContext;
    private View.OnClickListener listener;

    public WeaconAdapter(Context context, List<WeaconParse> weaconItemList) {
        this.weaconItemList = weaconItemList;
        this.mContext = context;
    }

    @Override
    public WeaconHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        v.setOnClickListener(this);
        WeaconHolder wh = new WeaconHolder(v);

        return wh;
    }

    @Override
    public void onBindViewHolder(WeaconHolder weaconHolder, int i) {
        WeaconParse weaconItem = weaconItemList.get(i);
//        String imageUrl = "file:" + weaconItem.getImagePath(); //Necessary for local files
        String imageUrl = weaconItem.getImageParseUrl();
        Picasso.with(mContext).load(imageUrl)
                .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                .placeholder(R.mipmap.ic_launcher)
                .into(weaconHolder.thumbnail);

//        weaconHolder.title.setText(Html.fromHtml(weaconItem.getTitle()));
        weaconHolder.title.setText(weaconItem.getName());
        weaconHolder.description.setText(weaconItem.getMessage());
        weaconHolder.itemView.setTag(weaconItem);
    }

    @Override
    public int getItemCount() {
        return (null != weaconItemList ? weaconItemList.size() : 0);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

}