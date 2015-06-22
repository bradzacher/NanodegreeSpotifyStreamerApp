package au.com.zacher.spotifystreamer.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.ActivityInitialiser;
import au.com.zacher.spotifystreamer.ToolbarOptions;
import au.com.zacher.spotifystreamer.adapter.SearchListAdapter;
import au.com.zacher.spotifystreamer.provider.SearchHistoryProvider;
import retrofit.RetrofitError;


public abstract class SearchActivity<T> extends ListActivity implements SearchView.OnQueryTextListener, android.support.v7.widget.Toolbar.OnMenuItemClickListener {
    private SearchListAdapter<T> listAdapter;
    private SearchView searchBox;

    private ProgressBar progressBar;
    private TextView noResultsText;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("SearchActivity");
        super.onCreate(savedInstanceState);
        // setup the toolbar
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = true;
        this.toolbar = ActivityInitialiser.initActivity(options, savedInstanceState, this, R.layout.activity_search);

        this.listAdapter = this.initListAdapter();
        this.setListAdapter(listAdapter);

        final Activity activity = this;
        ListView list = (ListView)this.findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchListAdapter.ViewHolder holder = listAdapter.getViewHolder(view);

                // successful query, so save if for quick usage next time
                SearchHistoryProvider provider = getSearchHistoryProvider();
                provider.addHistory(holder.getId(), holder.getName().getText().toString(), holder.getImageUrl());

                listAdapter.onItemClick(activity, view);
            }
        });

        this.progressBar = (ProgressBar)this.findViewById(R.id.activity_search_progress_bar);
        this.noResultsText = (TextView)this.findViewById(R.id.activity_search_no_results);

        this.buildListFromHistory();
    }

    private int searchCount = 0;
    private int incrementSearchCount() {
        return ++this.searchCount;
    }
    /**
     * Returns the current count for searches - to allow cancelling of queries and similar if a new search is made
     * @return
     */
    protected int getSearchCount() {
        return this.searchCount;
    }

    private void handleSearch() {
        final String originalQuery = searchBox.getQuery().toString();
        if (originalQuery.length() == 0) {
            this.buildListFromHistory();
            return;
        }

        String query = new String(originalQuery);

        final int currentSearchCount = this.incrementSearchCount();
        this.progressBar.setVisibility(View.VISIBLE);
        this.noResultsText.setVisibility(View.GONE);

        boolean hasAsterix = query.indexOf('*') >= 0;
        boolean hasDash = query.indexOf('-') >= 0;
        // make sure there's an astrix so we can get some decent search matches
        if (!hasAsterix && !hasDash) { // the api doco says that you can't add an asterix if there's a dash
            query += '*';
        }

        Logger.d(R.string.log_search_formatter, query);

        // so the user knows that loading is occurring
        listAdapter.clear();

        // for running back on the main thread
        final Handler mainHandler = new Handler(this.getApplicationContext().getMainLooper());

        // query the API
        HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("limit", 50);
        this.doQuery(query, queryParams, new QueryCallback<T>() {
            @Override
            public void success(final List<T> list) {
                if (getSearchCount() == currentSearchCount) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);

                            listAdapter.clear();
                            listAdapter.addAll(list);

                            if (list.size() > 0) {
                                getListView().setVisibility(View.VISIBLE);
                                noResultsText.setVisibility(View.GONE);
                            } else {
                                getListView().setVisibility(View.GONE);
                                noResultsText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (getSearchCount() == currentSearchCount) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.clear();
                        }
                    });
                }
                // TODO: display an error message
            }
        });
    }

    private void buildListFromHistory() {
        this.listAdapter.clear();

        // build an initial list from the user's history
        SearchHistoryProvider provider = this.getSearchHistoryProvider();
        List<String[]> history = provider.getHistory();
        if (history.size() > 0) {
            for (String[] item : history) {
                this.listAdapter.add(this.createBasicItem(item[0], item[1], item[2]));
            }
            this.getListView().setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //this.getMenuInflater().inflate(R.menu.menu_search_results, menu);
        this.toolbar.inflateMenu(R.menu.menu_search_results);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        this.searchBox = (SearchView) MenuItemCompat.getActionView(searchItem);
        this.searchBox.setOnQueryTextListener(this);
        this.searchBox.setQueryHint(this.getSearchQueryHint());
        this.searchBox.setIconified(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.handleSearch();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.handleSearch();
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }

    /**
     * Initialises the empty list adapter for this activity
     * @return
     */
    protected abstract SearchListAdapter<T> initListAdapter();
    /**
     * Performs a query using the given parameters, ensure you call callback.success and callback.failure in implementing classes
     * @param query
     * @param queryParams
     * @param callback
     */
    protected abstract void doQuery(String query, Map<String, Object> queryParams, QueryCallback<T> callback);
    /**
     * Gets the hint string to display on the search box
     * @return
     */
    protected abstract String getSearchQueryHint();
    /**
     * Gets the search history provider for the activity
     * @return
     */
    protected abstract SearchHistoryProvider getSearchHistoryProvider();
    /**
     * Creates a basic item for use as a search history item
     * @param description
     * @param imageUrl
     * @return
     */
    protected abstract T createBasicItem(String id, String description, String imageUrl);

    /**
     * For performing extra actions once a query returns
     * @param <T>
     */
    protected interface QueryCallback<T> {
        void success(List<T> list);

        void failure(RetrofitError error);
    }
}
