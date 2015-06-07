package au.com.zacher.spotifystreamer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import au.com.zacher.spotifystreamer.adapter.ArtistListAdapter;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;


public class SearchResultsActivity extends ListActivity {
    protected ArtistListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search_results);

        Logger.logActionCreate("MainActivity");

        listAdapter = new ArtistListAdapter(getApplicationContext(), R.layout.fragment_artist_search);
        this.setListAdapter(listAdapter);
        handleIntent();
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

            Log.d(getString(R.string.log_tag), String.format(getString(R.string.log_search_formatter), query));

            // TODO: spotify integration
            listAdapter.clear();
            listAdapter.addAll(
                new Artist(){{name = "Artist A"; id = "A"; images = new ArrayList<Image>();}},
                new Artist(){{name = "Artist B"; id = "B"; images = new ArrayList<Image>();}},
                new Artist(){{name = "Artist C"; id = "C"; images = new ArrayList<Image>();}}
            );
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
