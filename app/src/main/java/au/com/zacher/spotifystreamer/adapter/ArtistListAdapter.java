package au.com.zacher.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    }

    public ArtistListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ArtistListAdapter(Context context, int resource, Artist[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Artist item = this.getItem(position);

        // use ViewHolder pattern to speedup performance
        ViewHolder holder;
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
        holder.name.setText(item.name);
        if (!item.images.isEmpty()) {
            Picasso.with(getContext())
                    .load(item.images.get(0).url)
                    .placeholder(R.drawable.ic_music_note_black_48dp)
                    .error(R.drawable.ic_error_outline_black_48dp)
                    .into(holder.image);
        }

        return convertView;
    }
}
