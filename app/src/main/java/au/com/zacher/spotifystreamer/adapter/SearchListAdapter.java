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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Brad on 14/06/2015.
 */
public abstract class SearchListAdapter<T> extends ArrayAdapter<T> {
    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
    public static class ViewHolder {
        TextView name;
        ImageView image;
        int position;
        String imageUrl;
        String id;

        public TextView getName() {
            return name;
        }
        public ImageView getImage() {
            return image;
        }
        public int getPosition() {
            return position;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public String getId() {
            return id;
        }
    }

    public SearchListAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * Gets the viewHolder for the given view
     * @param view
     * @return
     */
    public ViewHolder getViewHolder(View view) {
        return (ViewHolder)view.getTag();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        T item = this.getItem(position);

        // use ViewHolder pattern to speedup performance
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.search_fragment_name);
            holder.image = (ImageView)convertView.findViewById(R.id.search_fragment_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // figure out the best image url
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

        // bind the values to the view
        holder.position = position;
        holder.name.setText(getItemText(item));
        holder.imageUrl = url;
        holder.id = getItemId(item);

        // we load the image into a background imageView first so we can check that the view hsan't been recycled before swapping it in
        // this will prevent accidents where artist images are shown for the incorrect artist if the image loads after recycling
        final ImageView backgroundLoadedImage = new ImageView(this.getContext());
        final String finalUrl = url;
        holder.image.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.ic_music_note_black_48dp));
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.ic_error_outline_black_48dp)
                .into(backgroundLoadedImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }

                    @Override
                    public void onError() {
                        Logger.e(R.string.log_image_load_error, finalUrl);
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }
                });

        return convertView;
    }

    /**
     * Actions user interaction with the list
     * @param clickedView
     */
    public void onItemClick(Activity activity, View clickedView) {
        SearchListAdapter.ViewHolder v = (SearchListAdapter.ViewHolder)clickedView.getTag();
        String id = this.getItemId(this.getItem(v.position));
        if (id != null) {
            // open the required view
            Intent i = new Intent(activity, this.getClickActivityClass())
                    .putExtra(this.getClickActivityExtraString(), id);
            this.getContext().startActivity(i);
        }
    }

    /**
     * Gets the text to display for an item
     * @param item
     * @return
     */
    protected abstract String getItemText(T item);
    /**
     * Gets the list of images for an item
     * @param item
     * @return
     */
    protected abstract List<Image> getItemImages(T item);
    /**
     * Gets the id for an item
     * @param item
     * @return
     */
    protected abstract String getItemId(T item);
    /**
     * Gets the class of the {@link android.app.Activity} to launch on click
     * @return
     */
    protected abstract Class getClickActivityClass();
    /**
     * Gets the unique string which identifies where the item id should be stored in the intent
     * @return
     */
    protected abstract String getClickActivityExtraString();
}
