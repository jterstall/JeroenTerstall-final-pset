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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;

import java.util.Stack;

// THe main activity
// This handles the side bar, user authentication and communication between fragments

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Stack used to keep track of current menu in side bar
    Stack<Integer> currentMenu = new Stack<>();

    NavigationView navigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set navigation drawer
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set listener
        navigationView.setNavigationItemSelectedListener(this);

        // Check if user is logged in
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // If user is logged in
                if(user != null)
                {
                    // Leave the navigation drawer unlocked
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    // Go to the home screen
                    HomeScreenFragment fragment = new HomeScreenFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // Add fragment menu id to the stack and set the correct navigation drawer item checked
                    currentMenu.push(Constants.HOME_STACK_INDEX);
                    navigationView.getMenu().getItem(Constants.HOME_STACK_INDEX).setChecked(true);
                }
                // If user is not logged in
                else
                {
                    // User may not use the navigation drawer
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    // Go to sign in screen
                    SignInFragment fragment = new SignInFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // Add fragment menu id to the stack and set the correct navigation drawer item checked
                    currentMenu.push(Constants.HOME_STACK_INDEX);
                    navigationView.getMenu().getItem(Constants.HOME_STACK_INDEX).setChecked(true);
                }

            }
        };
    }

    // On start add the authentication listener
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // When the app is closed stop listening for authentication changes
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // If drawer is open, close drawer on back press
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        // If drawer is not open but there are fragments to go back to, go to previous fragment on stack
        else if(fragmentManager.getBackStackEntryCount() > 0)
        {
            fragmentManager.popBackStack();
            currentMenu.pop();
            navigationView.getMenu().getItem(currentMenu.peek()).setChecked(true);
        }
        // IF none of the above just do the normal back press to quit the app
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

        // If statements to check which menu item is clicked and to set the correct fragments in the content frame
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

        // Close drawer after item is selected
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This function replaces the content frame with the search page results
    public void goToQueryResults(String type, JSONArray results)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create the fragment
        ShowQueryResultsFragment resultsFragment = new ShowQueryResultsFragment();

        // Set arguments to pass on to fragment
        Bundle args = new Bundle();
        args.putString("type", type);
        String results_query = results.toString();
        args.putString("results", results_query);
        resultsFragment.setArguments(args);

        // Replace screen with fragment and add to stack
        fragmentManager.beginTransaction().replace(R.id.content_frame, resultsFragment).addToBackStack(null).commit();
        currentMenu.push(Constants.SEARCH_STACK_INDEX);
    }

    // This function replaces the content frame with the album information page
    public void goToAlbumInfo(String artist, String album, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ALBUM, album);
        args.putString(Constants.JSON_ARTIST, artist);

        // Create fragment and set arguments
        ShowAlbumInfoFragment albumInfoFragment = new ShowAlbumInfoFragment();
        albumInfoFragment.setArguments(args);

        // Replace screen with fragment and add to stack
        fragmentManager.beginTransaction().replace(R.id.content_frame, albumInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    // This function replaces the content frame with the artist information page
    public void goToArtistInfo(String artist, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ARTIST, artist);

        // Create fragment and set arguments
        ShowArtistInfoFragment showArtistInfoFragment = new ShowArtistInfoFragment();
        showArtistInfoFragment.setArguments(args);

        // Replace screen with fragment and add to stack
        fragmentManager.beginTransaction().replace(R.id.content_frame, showArtistInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    // This function replaces the content frame with the track information page
    public void goToTrackInfo(String track, String artist, int stackIndex)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.JSON_ARTIST, artist);
        args.putString(Constants.JSON_TRACK, track);

        // Create fragment and set arguments
        ShowTrackInfoFragment showTrackInfoFragment = new ShowTrackInfoFragment();
        showTrackInfoFragment.setArguments(args);

        // Replace screen with fragment and add to stack
        fragmentManager.beginTransaction().replace(R.id.content_frame, showTrackInfoFragment).addToBackStack(null).commit();
        currentMenu.push(stackIndex);
    }

    // Function to go to the track collection page
    public void goToTrackCollection(String email, int index)
    {
        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);

        // Create fragment and set arguments
        TrackCollectionFragment trackCollectionFragment = new TrackCollectionFragment();
        trackCollectionFragment.setArguments(args);

        // Replace screen with fragment and add to stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, trackCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    // Function to go to the artist collection page
    public void goToArtistCollection(String email, int index)
    {
        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);

        // Create fragment and set arguments
        ArtistCollectionFragment artistCollectionFragment = new ArtistCollectionFragment();
        artistCollectionFragment.setArguments(args);

        // Replace screen with fragment and add to backstack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, artistCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    // Function to go to album collection page
    public void goToAlbumCollection(String email, int index)
    {
        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.EMAIL, email);

        // Create fragment and set arguments
        AlbumCollectionFragment albumCollectionFragment = new AlbumCollectionFragment();
        albumCollectionFragment.setArguments(args);

        // Replace screen with fragment and add to backstack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, albumCollectionFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }

    // Function to go to the register page
    public void goToRegisterPage()
    {
        // Replace screen with fragment and add to backstack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new RegisterFragment()).addToBackStack(null).commit();
        currentMenu.push(Constants.HOME_STACK_INDEX);
    }

    // Function to go to the user information page
    public void goToUserPage(String username, String email, int index)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create arguments to be passed on to fragment
        Bundle args = new Bundle();
        args.putString(Constants.USERNAME, username);
        args.putString(Constants.EMAIL, email);
        args.putInt(Constants.INDEX, index);

        // Create fragment and set arguments
        UserPageFragment userPageFragment = new UserPageFragment();
        userPageFragment.setArguments(args);

        // Replace screen with fragment and add to backstack
        fragmentManager.beginTransaction().replace(R.id.content_frame, userPageFragment).addToBackStack(null).commit();
        currentMenu.push(index);
    }
}
