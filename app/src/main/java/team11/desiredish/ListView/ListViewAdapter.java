package team11.desiredish.ListView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import team11.desiredish.R;

/**
 * Created by Yanjing on 10/22/14.
 */
public class ListViewAdapter extends ArrayAdapter<ListViewItem> {

    public ListViewAdapter(Context context, List<ListViewItem> items) {
        super(context, R.layout.myfood_listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.myfood_listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.list_image);
            viewHolder.restName = (TextView) convertView.findViewById(R.id.restaurant);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.rating = (RatingBar) convertView.findViewById(R.id.rate);
            viewHolder.isrest=(ImageView) convertView.findViewById(R.id.isrest);
            viewHolder.iscar=(ImageView) convertView.findViewById(R.id.iscar);

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ListViewItem item = getItem(position);
        viewHolder.image.setImageDrawable(item.image);
        viewHolder.restName.setText(item.restName);
        viewHolder.description.setText(item.description);
        viewHolder.time.setText(item.time);
        viewHolder.rating.setRating(Float.parseFloat(item.rating));
        if(item.restID.equals("null")||item.restID.equals("0")){
            viewHolder.isrest.setVisibility(View.INVISIBLE);
            viewHolder.iscar.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.isrest.setVisibility(View.VISIBLE);
            viewHolder.iscar.setVisibility(View.VISIBLE);
        }
        LayerDrawable stars = (LayerDrawable) viewHolder.rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(255, 102, 0), PorterDuff.Mode.SRC_ATOP);

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see //developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView image;
        TextView restName;
        TextView description;
        TextView time;
        RatingBar rating;
        ImageView isrest;
        ImageView iscar;
    }
}
