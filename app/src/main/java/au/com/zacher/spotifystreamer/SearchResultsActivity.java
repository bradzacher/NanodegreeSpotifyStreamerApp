package au.com.zacher.spotifystreamer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;


import java.util.ArrayList;

import au.com.zacher.spotifystreamer.adapter.ArtistListAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchResultsActivity extends ListActivity {
    protected ArtistListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_artist_search_results);

        Logger.logActionCreate("MainActivity");

        this.listAdapter = new ArtistListAdapter(this.getApplicationContext(), R.layout.fragment_artist_search);
        this.setListAdapter(listAdapter);
        this.handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handles the app intents
     */
    private void handleIntent() {
        handleIntent(getIntent());
    }
    /**
     * Handles the app intents
     */
    private void handleIntent(Intent intent) {
        Logger.logMethodCall("handleIntent(Intent)", "SearchResultsActivity");

        // handle the search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.indexOf('*') < 0) {
                query = '*' + query + '*';
            }

            Logger.d(R.string.log_search_formatter, query);

            // so the user knows that loading is occurring
            listAdapter.clear();
            listAdapter.add(new Artist() {{
                name = "Loading...";
                id = null;
                images = new ArrayList<Image>();
            }});

            // for running back on the main thread
            final Handler mainHandler = new Handler(this.getApplicationContext().getMainLooper());

            // query the API
            SpotifyApi api = new SpotifyApi();
            api.getService().searchArtists(query, new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    // TODO: spotify integration
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.clear();
                            listAdapter.addAll(artistsPager.artists.items);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Logger.e(R.string.log_api_error, error.getUrl(), error.getResponse().getStatus(), error.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.clear();
                            // TODO: display an error message
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "SearchResultsActivity");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
