package au.com.zacher.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Brad on 22/06/2015.
 */
public class ActivityInitialiser {
    private ActivityInitialiser() { }

    /**
     * Sets up the view and toolbar
     * @param savedInstanceState
     * @param activity
     * @param layoutId
     * @return
     */
    public static Toolbar initActivity(ToolbarOptions options, Bundle savedInstanceState, final Activity activity, int layoutId) {
        Toolbar toolbar;
        AppCompatDelegate compatDelegate;

        if (activity instanceof AppCompatActivity) {
            activity.setContentView(layoutId);
            compatDelegate = ((AppCompatActivity)activity).getDelegate();
        } else {
            // setup the toolbar
            AppCompatCallback callback = new AppCompatCallback() {
                @Override
                public void onSupportActionModeStarted(ActionMode actionMode) {
                    // TODO
                }

                @Override
                public void onSupportActionModeFinished(ActionMode actionMode) {
                    // TODO
                }

                @Nullable
                @Override
                public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                    // TODO
                    return null;
                }
            };
            compatDelegate = AppCompatDelegate.create(activity, callback);
            compatDelegate.onCreate(savedInstanceState);
            compatDelegate.setContentView(layoutId);
        }
        toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);
        ActionBar actionbar = compatDelegate.getSupportActionBar();

        /*if (activity instanceof Toolbar.OnMenuItemClickListener) {
            toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener)activity);
        }
        if (activity instanceof Toolbar.OnClickListener) {
            toolbar.setOnClickListener((Toolbar.OnClickListener)activity);
        }*/

        if (options.enableUpButton) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }

        return toolbar;
    }
}
