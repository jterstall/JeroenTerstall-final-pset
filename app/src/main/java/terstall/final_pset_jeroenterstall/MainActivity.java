package terstall.final_pset_jeroenterstall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    static String TRACK_TYPE = "Track";
    static String ARTIST_TYPE = "Artist";
    static String ALBUM_TYPE = "Album";

    int SEARCH_STACK_INDEX = 1;
    int HOME_STACK_INDEX = 0;
    int COLLECTION_STACK_INDEX = 2;

    Stack<Integer> currentMenu = new Stack<>();

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if(savedInstanceState == null)
        {
            HomeScreenFragment fragment = new HomeScreenFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
            currentMenu.push(HOME_STACK_INDEX);
            navigationView.getMenu().getItem(HOME_STACK_INDEX).setChecked(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fragmentManager.getBackStackEntryCount() > 0)
        {
            fragmentManager.popBackStack();
            currentMenu.pop();
            navigationView.getMenu().getItem(currentMenu.peek()).setChecked(true);
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_search_function)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchFragment()).addToBackStack(null).commit();
            currentMenu.push(SEARCH_STACK_INDEX);
        }
        else if (id == R.id.nav_home_screen)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeScreenFragment()).addToBackStack(null).commit();
            currentMenu.push(HOME_STACK_INDEX);
        }
        else if (id == R.id.nav_music_collection)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new CollectionFragment()).addToBackStack(null).commit();
            currentMenu.push(COLLECTION_STACK_INDEX);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToQueryResults(String type, JSONArray results)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShowQueryResultsFragment resultsFragment = new ShowQueryResultsFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        String results_query = results.toString();
        args.putString("results", results_query);
        resultsFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, resultsFragment).addToBackStack(null).commit();
        currentMenu.push(SEARCH_STACK_INDEX);
    }

    public void goToMusicInfo(String type, JSONObject query_result) throws JSONException
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        if(type.equals(TRACK_TYPE))
        {
            String track = (String) query_result.get(RetrieveApiInformationTask.JSON_NAME);
            String artist = (String) query_result.get(RetrieveApiInformationTask.JSON_ARTIST);
            args.putString("track", track);
            args.putString("artist", artist);
            ShowTrackInfoFragment trackInfoFragment = new ShowTrackInfoFragment();
            trackInfoFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, trackInfoFragment).addToBackStack(null).commit();
        }
        else if(type.equals(ARTIST_TYPE))
        {
            String artist = (String) query_result.get(RetrieveApiInformationTask.JSON_NAME);
            args.putString("artist", artist);
            ShowArtistInfoFragment artistInfoFragment = new ShowArtistInfoFragment();
            artistInfoFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, artistInfoFragment).addToBackStack(null).commit();
        }
        else if(type.equals(ALBUM_TYPE))
        {
            String artist = (String) query_result.get(RetrieveApiInformationTask.JSON_ARTIST);
            String album = (String) query_result.get(RetrieveApiInformationTask.JSON_NAME);
            args.putString("album", album);
            args.putString("artist", artist);
            ShowAlbumInfoFragment albumInfoFragment = new ShowAlbumInfoFragment();
            albumInfoFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, albumInfoFragment).addToBackStack(null).commit();
        }
        currentMenu.push(SEARCH_STACK_INDEX);
    }
}
