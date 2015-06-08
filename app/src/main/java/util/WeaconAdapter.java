package util;

/**
 * Created by Milenko on 04/06/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeaconAdapter extends RecyclerView.Adapter<WeaconHolder> implements View.OnClickListener {

    private List<WeaconItem> weaconItemList;
    private Context mContext;
    private View.OnClickListener listener;

    public WeaconAdapter(Context context, List<WeaconItem> weaconItemList) {
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
        WeaconItem weaconItem = weaconItemList.get(i);
        String url = "file:" + weaconItem.getThumbnail(); //Necessary for local files
        Picasso.with(mContext).load(url)
                .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                .placeholder(R.drawable.abc_btn_check_to_on_mtrl_015)
                .into(weaconHolder.thumbnail);

//        weaconHolder.title.setText(Html.fromHtml(weaconItem.getTitle()));
        weaconHolder.title.setText(weaconItem.getTitle());
        weaconHolder.description.setText(weaconItem.getMessage());
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