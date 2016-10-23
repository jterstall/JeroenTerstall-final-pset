package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

// This fragment shows the artist information page

public class ShowArtistInfoFragment extends Fragment
{
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference ref;

    MainActivity activity;
    View mView;

    Artist mArtist;
    JSONObject artist_data;

    String artist;
    boolean firstClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_artist_info_layout, container, false);

        // Get user authentication intance
        mAuth = FirebaseAuth.getInstance();

        // Retrieve arguments passed
        Bundle args = getArguments();
        artist = args.getString(Constants.JSON_ARTIST);

        try
        {
            // Retrieve artist data and set views with it
            retrieveArtistData();
            setArtistData();
        }
        catch (MalformedURLException | ExecutionException | InterruptedException | JSONException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        // Connect to the database and set references
        connectDB();

        // Flag to check for first run
        firstClick = true;

        // Set listeners
        setDBListeners();

        return mView;
    }

    // Retrieve json from lastfm api about artist
    private void retrieveArtistData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        artist = URLEncoder.encode(artist, "UTF-8");
        URL url = new URL(Constants.GET_ARTIST_URL + artist + Constants.API_KEY);
        artist_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(Constants.JSON_ARTIST);
    }

    // This function sets all view with correct json results
    private void setArtistData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.artist_info_title);
        TextView summaryView = (TextView) mView.findViewById(R.id.artist_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.artist_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.artist_info_image);

        // SSet artist name
        String artist = (String) artist_data.get(Constants.JSON_NAME);
        artistView.setText(artist);

        // Set summary with clickable links
        String summary = (String) artist_data.getJSONObject(Constants.JSON_BIO).get(Constants.JSON_SUMMARY);
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml(summary));

        // Set image of artist
        String image_url = (String) artist_data.getJSONArray(Constants.JSON_IMAGE).getJSONObject(Constants.JSON_IMAGE_SIZE).get(Constants.JSON_IMAGE_URL);
        if(!image_url.isEmpty())
        {
            Picasso.with(activity).load(image_url).into(imageView);
        }
        else
        {
            imageView.setImageResource(R.drawable.no_image);
        }

        // Set the tags
        String tags_content = "";
        JSONArray tags = artist_data.getJSONObject(Constants.JSON_TAGS).getJSONArray(Constants.JSON_TAG);
        for(int i = 0; i < tags.length(); i++)
        {
            if(i == 0)
            {
                tags_content = (String) tags.getJSONObject(i).get(Constants.JSON_NAME);
            }
            else
            {
                tags_content = tags_content + ", " + tags.getJSONObject(i).get(Constants.JSON_NAME);
            }
        }
        tagsView.setText(tags_content);

        // Retrieve url to lastfm website which is an unique id we use to identify artist in database
        String url = (String) artist_data.get(Constants.JSON_URL);

        // Create artist with data retrieved
        mArtist = new Artist(artist, summary, tags_content, image_url, url);
    }

    // Function to connect to the database and set the correct storage reference
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ","));
        ref = ref.child(Constants.JSON_ARTIST);
    }

    // Set listeners to handle database entries and add icon state
    private void setDBListeners()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.artist_info_add);

        // Retrieve unique url and replace illegal characters with comma
        final String url_id = mArtist.getUrl().replaceAll("[./#$\\[\\]]", ",");

        // Listen for clicks on add icon
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DatabaseReference childRef = ref.child(url_id);
                childRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Check if url has an entry in database
                        if(dataSnapshot.getValue() == null)
                        {
                            // If not and not first run, add to database and set icon state
                            if(!firstClick)
                            {
                                childRef.setValue(mArtist);
                                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                            }
                            // If not and first run, only change icon state
                            else
                            {
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            firstClick = false;
                        }
                        else
                        {
                            // If it has an entry and not first run, remove from database and alter icon state
                            if(!firstClick)
                            {
                                childRef.removeValue();
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            // If it has an entry and first run, only alter add icon state
                            else
                            {
                                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                            }
                            firstClick = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });
        // Perform a click to correctly set add icon state
        add.performClick();
    }

    // Retrieve main activity if fragment is attached to call functions from main activity
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            activity = (MainActivity) this.getActivity();
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
    }
}
