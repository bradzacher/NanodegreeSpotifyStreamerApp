package au.com.zacher.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.ActivityInitialiser;
import au.com.zacher.spotifystreamer.ToolbarOptions;
import au.com.zacher.spotifystreamer.activity.fragment.ScreenWidthCardList;
import au.com.zacher.spotifystreamer.model.DisplayItem;

public class MainActivity extends AppCompatActivity implements android.support.v7.widget.Toolbar.OnMenuItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.setContext(getApplicationContext());
        Logger.logActionCreate("MainActivity");
        super.onCreate(savedInstanceState);
        // setup the toolbar and contentView
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = false;
        ActivityInitialiser.initActivity(options, savedInstanceState, this, R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ScreenWidthCardList newReleases = (ScreenWidthCardList)fragmentManager.findFragmentById(R.id.new_releases_fragment);
        newReleases.setTitle("New Releases");
        newReleases.setItems(new DisplayItem[]{
                new DisplayItem("-1", "https://i.scdn.co/image/d181666ebeda1aa062a5f0250b85be6150badd0f", "TEST"),
                new DisplayItem("-1", "https://i.scdn.co/image/d181666ebeda1aa062a5f0250b85be6150badd0f", "TEST"),
                new DisplayItem("-1", "https://i.scdn.co/image/d181666ebeda1aa062a5f0250b85be6150badd0f", "TEST"),
                new DisplayItem("-1", "https://i.scdn.co/image/d181666ebeda1aa062a5f0250b85be6150badd0f", "TEST")
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "MainActivity");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search_artist) {
            Intent i = new Intent(this, ArtistSearchActivity.class);
            this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }
}
