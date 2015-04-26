package uk.co.jamesroutley.flower.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.jamesroutley.flower.R;

/**
 * Created by admin on 17/03/15.
 */
public class FlowerAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public FlowerAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        // your particular dataset uses String IDs
        // but you have to put something in this method
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.list_row, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.image);
            holder.commonNameTextView = (TextView) convertView.findViewById(R.id.name);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current book's data in JSON form
        JSONObject jsonObject = (JSONObject) getItem(position);

        String imageUrl = "http://10.0.3.2:5000/img/" + jsonObject.optString("image");

        // Use Picasso to load the image
        // Temporarily have a placeholder in case it's slow to load
        Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.progress_animation).into(holder.thumbnailImageView);


        // Grab the text info from the JSON
        String flowerName = "";


        if (jsonObject.has("common_name")) {
            flowerName = jsonObject.optString("common_name");
        }

        // Send String to the TextViews for display
        holder.commonNameTextView.setText(flowerName);

        return convertView;
    }

    // this is used so you only ever have to do
    // inflation and finding by ID once ever per View
    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView commonNameTextView;
    }

    public void updateData(JSONArray jsonArray) {
        // update the adapter's dataset
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

}
