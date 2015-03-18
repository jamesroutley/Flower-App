package uk.co.jamesroutley.flower.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import uk.co.jamesroutley.flower.FlowerResult.FlowerResult;
import uk.co.jamesroutley.flower.R;
import uk.co.jamesroutley.flower.app.AppController;

/**
 * Created by admin on 20/01/15.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FlowerResult> flowerResultItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<FlowerResult> flowerResultItems) {
        this.activity = activity;
        this.flowerResultItems = flowerResultItems;
    }

    @Override
    public int getCount() {
        return flowerResultItems.size();
    }

    @Override
    public Object getItem(int location) {
        return flowerResultItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);


        // getting movie data for the row
        FlowerResult m = flowerResultItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        return convertView;
    }

}