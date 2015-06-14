package au.com.zacher.spotifystreamer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import au.com.zacher.spotifystreamer.R;


public class ArtistViewActivity extends Activity {
    public static final String INTENT_EXTRA;
    static {
        Class c = ArtistViewActivity.class;
        INTENT_EXTRA = c.getPackage() + ".IntentExtra." + c.getName() + ".ArtistId";
    }
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_view);

        // fetch the artist ID from the intent
        Intent i = this.getIntent();
        this.artistId = i.getStringExtra(ArtistViewActivity.INTENT_EXTRA);

        ((TextView)this.findViewById(R.id.test)).setText(this.artistId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
