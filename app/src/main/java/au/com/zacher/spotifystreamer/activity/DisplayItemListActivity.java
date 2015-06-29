package au.com.zacher.spotifystreamer.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.zacher.spotifystreamer.ActivityInitialiser;
import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.ToolbarOptions;
import au.com.zacher.spotifystreamer.adapter.DisplayItemListAdapter;

/**
 * Created by Brad on 29/06/2015.
 */
public abstract class DisplayItemListActivity<T> extends ListActivity implements android.support.v7.widget.Toolbar.OnMenuItemClickListener {
    private Toolbar toolbar;
    private DisplayItemListAdapter<T> listAdapter;
    private ProgressBar progressBar;
    private TextView noResultsText;

    /**
     * Convenience method to get the action bar as a {@link Toolbar}
     */
    public Toolbar getToolbar() {
        return this.toolbar;
    }
    /**
     * Convenience method to get the list adapter as a {@link DisplayItemListAdapter}
     */
    public DisplayItemListAdapter<T> getDisplayItemListAdapter() {
        return this.listAdapter;
    }
    /**
     * Gets the {@link android.widget.ProgressBar} instance from the view
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    /**
     * Gets the no results {@link android.widget.TextView} on the Activity
     */
    public TextView getNoResultsText() {
        return noResultsText;
    }

    private ArrayList<AdapterView.OnItemClickListener> clickListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("DisplayItemListActivity");
        super.onCreate(savedInstanceState);
        // setup the toolbar
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = true;
        this.toolbar = ActivityInitialiser.initActivity(options, savedInstanceState, this, R.layout.activity_display_item_list);

        this.listAdapter = this.initListAdapter();
        this.setListAdapter(listAdapter);

        final Activity activity = this;
        ListView list = (ListView)this.findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (AdapterView.OnItemClickListener l : clickListeners) {
                    l.onItemClick(parent, view, position, id);
                }
                listAdapter.onItemClick(activity, view);
            }
        });

        this.progressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
        this.noResultsText = (TextView)this.findViewById(R.id.no_results_text);

        if (this.progressBar == null) {
            throw new RuntimeException("Your content must have a ProgressBar whose id attribute is 'R.id.progress_bar' ('@id/progress_bar').");
        }
        if (this.noResultsText == null) {
            throw new RuntimeException("Your content must have a TextView whose id attribute is 'R.id.no_results_text' ('@id/no_results_text).");
        }

        this.noResultsText.setText(this.getResources().getString(this.getNoResultsTextId()));
    }

    /**
     * Adds a {@link AdapterView.OnItemClickListener} to the list
     */
    public void addOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.clickListeners.add(listener);
    }

    /**
     * Removes the specified {@link AdapterView.OnItemClickListener} from the list
     */
    @SuppressWarnings("unused")
    public void removeOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.clickListeners.remove(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.toolbar.inflateMenu(this.getToolbarMenuId());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }

    /**
     * Toggles the visibility of the list, progress bar and no-results text based on the input
     */
    public void setProgressStatus(ProgressStatus status) {
        switch (status) {
            case LIST_READY:
                this.getListView().setVisibility(View.VISIBLE);
                this.getProgressBar().setVisibility(View.GONE);
                this.getNoResultsText().setVisibility(View.GONE);
                break;

            case SEARCHING:
                this.getListView().setVisibility(View.GONE);
                this.getProgressBar().setVisibility(View.VISIBLE);
                this.getNoResultsText().setVisibility(View.GONE);
                break;

            case NO_RESULTS:
                this.getListView().setVisibility(View.GONE);
                this.getProgressBar().setVisibility(View.GONE);
                this.getNoResultsText().setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Initialises the empty {@link DisplayItemListAdapter} for this activity
     */
    protected abstract DisplayItemListAdapter<T> initListAdapter();
    /**
     * Gets the id of the toolbar layout to expand into the view
     */
    protected abstract int getToolbarMenuId();
    /**
     * Gets the id of the string to show in the no results text view
     */
    protected abstract int getNoResultsTextId();

    public enum ProgressStatus {
        SEARCHING, LIST_READY, NO_RESULTS
    }
}
