package au.com.zacher.spotifystreamer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import au.com.zacher.spotifystreamer.model.DisplayItem;
import au.com.zacher.spotifystreamer.model.DisplayItemViewHolder;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.Utilities;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Brad on 14/06/2015.
 */
public abstract class SearchListAdapter<T> extends ArrayAdapter<DisplayItem> {
    public SearchListAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * Gets the viewHolder for the given view
     */
    public DisplayItemViewHolder getViewHolder(View view) {
        return (DisplayItemViewHolder)view.getTag();
    }

    /**
     * Adds a Display item for the given item
     * @param item the item to add
     */
    public void addItem(T item) {
        List<Image> images = getItemImages(item);
        String url = null;
        if (!images.isEmpty()) {
            Image closestImage = images.get(0);

            // no point doing math if we've only got 1 image
            if (images.size() > 1) {
                // find the image that most closely matches our required size for efficiency
                float imageSize = getContext().getResources().getDimension(R.dimen.search_fragment_image_size);
                float minDifH = Float.MAX_VALUE;
                float minDifW = Float.MAX_VALUE;
                for (Image image : images) {
                    float difH = image.height - imageSize;
                    float difW = image.width - imageSize;

                    // we only want images bigger than our required size
                    if (difH > 0 && difW > 0) {
                        if (difH < minDifH || difW < minDifW) {
                            closestImage = image;
                            minDifH = difH;
                            minDifW = difW;
                        }
                    }
                }
            }

            url = closestImage.url;
        }

        DisplayItem displayItem = new DisplayItem(this.getItemId(item), url, this.getItemTitle(item), this.getItemSubtitle(item));
        this.add(displayItem);
    }

    /**
     * Adds a DisplayItem for each item in the collection
     * @param collection the collection of items
     */
    public void addAllItems(Collection<? extends T> collection) {
        for (T item : collection) {
            this.addItem(item);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DisplayItem item = this.getItem(position);

        // use ViewHolder pattern to speedup performance
        final DisplayItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_item, parent, false);
            holder = new DisplayItemViewHolder(convertView);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.subtitle = (TextView)convertView.findViewById(R.id.subtitle);
            holder.image = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = this.getViewHolder(convertView);
        }

        // bind the values to the view
        holder.position = position;
        holder.title.setText(item.title);
        holder.subtitle.setText(item.subtitle);
        holder.item = item;

        Utilities.backgroundLoadImage(this.getContext(), item, holder, position);

        return convertView;
    }

    /**
     * Actions user interaction with the list
     * @param activity the activity to launch
     * @param clickedView the view that was clicked on
     */
    public void onItemClick(Activity activity, View clickedView) {
        DisplayItemViewHolder v = this.getViewHolder(clickedView);
        String id = this.getItem(v.position).id;
        if (id != null) {
            // open the required view
            Intent i = new Intent(activity, this.getClickActivityClass())
                    .putExtra(this.getClickActivityExtraString(), id);
            this.getContext().startActivity(i);
        }
    }

    /**
     * Gets the title text to display for an item
     */
    protected abstract String getItemTitle(T item);
    /**
     * Gets the subtitle text to display for an item
     */
    protected abstract String getItemSubtitle(T item);
    /**
     * Gets the list of images for an item
     */
    protected abstract List<Image> getItemImages(T item);
    /**
     * Gets the id for an item
     */
    protected abstract String getItemId(T item);
    /**
     * Gets the class of the {@link android.app.Activity} to launch on click
     */
    protected abstract Class getClickActivityClass();
    /**
     * Gets the unique string which identifies where the item id should be stored in the intent
     */
    protected abstract String getClickActivityExtraString();
}
