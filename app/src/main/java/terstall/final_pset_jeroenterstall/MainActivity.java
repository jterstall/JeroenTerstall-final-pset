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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;

import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Stack<Integer> currentMenu = new Stack<>();

    NavigationView navigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    HomeScreenFragment fragment = new HomeScreenFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    currentMenu.push(Constants.HOME_STACK_INDEX);
                    navigationView.getMenu().getItem(Constants.HOME_STACK_INDEX).setChecked(true);
                }
                else
                {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    SignInFragment fragment = new SignInFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    currentMenu.push(Constants.HOME_STACK_INDEX);
                    navigationView.getMenu().getItem(Constants.HOME_STACK_INDEX).setChecked(true);
                }

            }
        };
    }

    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
            currentMenu.push(Constants.SEARCH_STACK_INDEX);
        }
        else if (id == R.id.nav_home_screen)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeScreenFragment()).addToBackStack(null).commit();
            currentMenu.push(Constants.HOME_STACK_INDEX);
        }
        else if (id == R.id.nav_music_collection)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new CollectionFragment()).addToBackStack(null).commit();
            currentMenu.push(Constants.COLLECTION_STACK_INDEX);
        }
        else if (id == R.id.nav_user_search_function)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchUsersFragment()).addToBackStack(null).commit();
            currentMenu.push(Constants.SEARCH_USER_STACK_INDEX);
        }
        else if (id == R.id.nav_user_collection)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new UserCollectionFragment()).addToBackStack(null).commit();
            currentMenu.push(Constants.USER_COLLECTION_STACK_INDEX);
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
        currentMenu.push(Constants.SEARCH_STACK_INDEX);
    }

    public void goToAlbumInfo(String artist, String album, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ALBUM, album);
        args.putString(Constants.JSON_ARTIST, artist);
        ShowAlbumInfoFragment albumInfoFragment = new ShowAlbumInfoFragment();
        albumInfoFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, albumInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    public void goToArtistInfo(String artist, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ARTIST, artist);
        ShowArtistInfoFragment showArtistInfoFragment = new ShowArtistInfoFragment();
        showArtistInfoFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, showArtistInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    public void goToTrackInfo(String track, String artist, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ARTIST, artist);
        args.putString(Constants.JSON_TRACK, track);
        ShowTrackInfoFragment showTrackInfoFragment = new ShowTrackInfoFragment();
        showTrackInfoFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, showTrackInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    public void goToTrackCollection(String email, int index)
    {
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);
        TrackCollectionFragment trackCollectionFragment = new TrackCollectionFragment();
        trackCollectionFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, trackCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    public void goToArtistCollection(String email, int index)
    {
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);
        ArtistCollectionFragment artistCollectionFragment = new ArtistCollectionFragment();
        artistCollectionFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, artistCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    public void goToAlbumCollection(String email, int index)
    {
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);
        AlbumCollectionFragment albumCollectionFragment = new AlbumCollectionFragment();
        albumCollectionFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, albumCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    public void goToRegisterPage()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new RegisterFragment()).addToBackStack(null).commit();
        currentMenu.push(Constants.HOME_STACK_INDEX);
    }

    public void goToUserPage(String username, String email, int index)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(Constants.USERNAME, username);
        args.putString(Constants.EMAIL, email);
        args.putInt(Constants.INDEX, index);
        UserPageFragment userPageFragment = new UserPageFragment();
        userPageFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, userPageFragment).addToBackStack(null).commit();
        currentMenu.push(index);

    }
}
