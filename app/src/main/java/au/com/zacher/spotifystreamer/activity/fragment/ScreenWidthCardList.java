package au.com.zacher.spotifystreamer.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.Utilities;
import au.com.zacher.spotifystreamer.model.DisplayItem;
import au.com.zacher.spotifystreamer.model.DisplayItemViewHolder;

/**
 * Created by Brad on 17/06/2015.
 */
public class ScreenWidthCardList extends Fragment {
    private RecyclerView recyclerView;
    private TextView titleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundleInstance) {
        View view = inflater.inflate(R.layout.fragment_screen_width_list, container, false);

        this.recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);

        this.recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        this.recyclerView.setAdapter(new CardListAdapter(new DisplayItem[0]));

        this.titleView = (TextView)view.findViewById(R.id.title);

        return view;
    }

    /**
     * Sets the list of items
     * @param items the items to use
     */
    public void setItems(DisplayItem[] items) {
        this.recyclerView.setAdapter(new CardListAdapter(items));
        this.recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
    }

    /**
     * Sets the title text of the list
     * @param text the text title
     */
    public void setTitle(String text) {
        this.titleView.setText(text);
    }

    private class CardListAdapter extends RecyclerView.Adapter<DisplayItemViewHolder> {
        private DisplayItem[] items;

        public CardListAdapter(DisplayItem[] items) {
            this.items = items;
        }

        @Override
        public DisplayItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.fragment_grid_item, parent, false);
            return new DisplayItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final DisplayItemViewHolder holder, final int position) {
            final DisplayItem item = this.items[position];
            holder.title.setText(item.title);
            if (item.subtitle == null) {
                holder.subtitle.setVisibility(View.GONE);
            } else {
                holder.subtitle.setText(item.subtitle);
                holder.subtitle.setVisibility(View.VISIBLE);
            }
            holder.position = position;

            // load the image
            Utilities.backgroundLoadImage(getActivity(), item, holder, position);
        }

        @Override
        public int getItemCount() {
            return this.items.length;
        }
    }
}
