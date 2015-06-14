package au.com.zacher.spotifystreamer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Brad on 7/06/2015.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist> {
    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
    private static class ViewHolder {
        TextView name;
        ImageView image;
        int position;
    }

    public ArtistListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ArtistListAdapter(Context context, int resource, Artist[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Artist item = this.getItem(position);

        // use ViewHolder pattern to speedup performance
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_artist_search, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.artist_search_fragment_name);
            holder.image = (ImageView)convertView.findViewById(R.id.artist_search_fragment_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // bind the values to the view
        holder.position = position;
        holder.name.setText(item.name);
        String url = null;
        if (!item.images.isEmpty()) {
            url = item.images.get(0).url;
        }
        // we load the image into a background imageView first so we can check that the view hsan't been recycled before swapping it in
        // this will prevent accidents where artist images are shown for the incorrect artist if the image loads after recycling
        final ImageView backgroundLoadedImage = new ImageView(this.getContext());
        holder.image.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.ic_music_note_black_48dp));
        final String finalUrl = url;
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.ic_error_outline_black_48dp)
                .into(backgroundLoadedImage, new Callback() {
                    // we need to make sure that the view hasn't been recycled before swapping the loaded image in
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
}
