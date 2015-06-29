package au.com.zacher.spotifystreamer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.SearchView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.adapter.DisplayItemListAdapter;
import au.com.zacher.spotifystreamer.model.DisplayItem;
import au.com.zacher.spotifystreamer.model.DisplayItemViewHolder;
import au.com.zacher.spotifystreamer.data.helper.SearchHistoryDbHelper;
import retrofit.RetrofitError;


public abstract class SearchActivity<T> extends DisplayItemListActivity<T> implements SearchView.OnQueryTextListener {
    private SearchView searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("SearchActivity");
        super.onCreate(savedInstanceState);

        this.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisplayItemViewHolder holder = getDisplayItemListAdapter().getViewHolder(view);

                // successful query, so save if for quick usage next time
                SearchHistoryDbHelper provider = getSearchHistoryProvider();
                provider.addHistory(holder.item);
            }
        });

        this.buildListFromHistory();
    }

    private int searchCount = 0;
    private int incrementSearchCount() {
        return ++this.searchCount;
    }
    /**
     * Returns the current count for searches - to allow cancelling of queries and similar if a new search is made
     */
    protected int getSearchCount() {
        return this.searchCount;
    }

    private void handleSearch() {
        String query = searchBox.getQuery().toString();
        if (query.length() == 0) {
            this.buildListFromHistory();
            return;
        }

        final int currentSearchCount = this.incrementSearchCount();
        this.setProgressStatus(ProgressStatus.SEARCHING);

        boolean hasAsterix = query.indexOf('*') >= 0;
        boolean hasDash = query.indexOf('-') >= 0;
        // make sure there's an astrix so we can get some decent search matches
        if (!hasAsterix && !hasDash) { // the api doco says that you can't add an asterix if there's a dash
            query += '*';
        }

        Logger.d(R.string.log_search_formatter, query);

        // so the user knows that loading is occurring
        this.getDisplayItemListAdapter().clear();

        // for running back on the main thread
        final Handler mainHandler = new Handler(this.getApplicationContext().getMainLooper());

        // query the API
        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put("limit", 50);
        this.doQuery(query, queryParams, new QueryCallback<T>() {
            @Override
            public void success(final List<T> list) {
                if (getSearchCount() == currentSearchCount) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            DisplayItemListAdapter<T> adapter = getDisplayItemListAdapter();
                            adapter.clear();
                            adapter.addAllItems(list);

                            if (list.size() > 0) {
                                setProgressStatus(ProgressStatus.LIST_READY);
                            } else {
                                setProgressStatus(ProgressStatus.NO_RESULTS);
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
                            getDisplayItemListAdapter().clear();
                        }
                    });
                }
                // TODO: display an error message
            }
        });
    }

    private void buildListFromHistory() {
        this.getDisplayItemListAdapter().clear();

        // build an initial list from the user's history
        SearchHistoryDbHelper provider = this.getSearchHistoryProvider();
        List<DisplayItem> history = provider.getHistory();
        if (history.size() > 0) {
            this.getDisplayItemListAdapter().addAll(history);
            this.setProgressStatus(ProgressStatus.LIST_READY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        this.searchBox = (SearchView) MenuItemCompat.getActionView(searchItem);
        this.searchBox.setOnQueryTextListener(this);
        this.searchBox.setQueryHint(this.getSearchQueryHint());
        this.searchBox.setIconified(false);

        return res;
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
    protected int getToolbarMenuId() {
        return R.menu.menu_search_results;
    }

    /**
     * Performs a query using the given parameters, ensure you call {@link QueryCallback#success(List)} and {@link QueryCallback#failure(RetrofitError)} on the callback parameter in implementing classes
     * @param query the query to perform
     * @param queryParams the parameters for the query
     * @param callback the callback
     */
    protected abstract void doQuery(String query, Map<String, Object> queryParams, QueryCallback<T> callback);
    /**
     * Gets the hint string to display on the {@link SearchView}
     */
    protected abstract String getSearchQueryHint();
    /**
     * Gets the {@link SearchHistoryDbHelper} for the activity
     */
    protected abstract SearchHistoryDbHelper getSearchHistoryProvider();

    /**
     * For performing extra actions once a query returns
     * @param <T>
     */
    protected interface QueryCallback<T> {
        void success(List<T> list);

        void failure(RetrofitError error);
    }
}
