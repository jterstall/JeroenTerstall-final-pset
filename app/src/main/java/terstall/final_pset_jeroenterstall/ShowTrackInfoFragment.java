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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class ShowTrackInfoFragment extends Fragment
{
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference ref;

    MainActivity activity;
    View mView;

    Track mTrack;
    JSONObject track_data;

    String track;
    String artist;

    boolean firstClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_track_info_layout, container, false);

        // Get user authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Retrieve passed arguments
        Bundle args = getArguments();
        artist = args.getString(Constants.JSON_ARTIST);
        track = args.getString(Constants.JSON_TRACK);

        try
        {
            // Retrieve track data and set views with it
            retrieveTrackData();
            setTrackData();
        }
        catch (MalformedURLException | ExecutionException | JSONException | InterruptedException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        // Connect to database and set references to storage
        connectDB();

        // Flag for setting image resource on first run
        firstClick = true;

        // Set listeners
        setDBListeners();

        return mView;
    }

    // This function calls the api and retrieves JSON from it
    private void retrieveTrackData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        track = URLEncoder.encode(track, "UTF-8");
        artist = URLEncoder.encode(artist, "UTF-8");
        URL url = new URL(Constants.GET_TRACK_URL + "track=" + track+ "&artist=" + artist + Constants.API_KEY);
        track_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(Constants.JSON_TRACK);
    }

    // This function sets the views with corresponding json data
    private void setTrackData() throws JSONException
    {
        // Retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.track_info_artist);
        TextView trackView = (TextView) mView.findViewById(R.id.track_info_title);
        TextView summaryView = (TextView) mView.findViewById(R.id.track_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.track_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.track_info_image);

        // Set image url
        imageView.setImageResource(R.drawable.no_image);

        // Set track name
        String track = (String) track_data.get(Constants.JSON_NAME);
        trackView.setText(track);

        // Set artist name
        String artist = (String) track_data.getJSONObject(Constants.JSON_ARTIST).get(Constants.JSON_NAME);
        artistView.setText(artist);

        // Set the tags
        String tags_content = "";
        JSONArray tags = track_data.getJSONObject(Constants.JSON_TOPTAGS).getJSONArray(Constants.JSON_TAG);
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

        // Set summary with clickable links
        String summary = "";
        if(track_data.has(Constants.JSON_WIKI))
        {
            summary = (String) track_data.getJSONObject(Constants.JSON_WIKI).get(Constants.JSON_SUMMARY);
        }
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml(summary));

        // Retrieve unique url, used to store track in database
        String url = (String) track_data.get(Constants.JSON_URL);

        // Create Track Object
        mTrack = new Track(track, artist, summary, tags_content, url);
    }

    // Function to connect to the database and set correct references
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ","));
        ref = ref.child(Constants.JSON_TRACK);
    }

    // Set listener to change add icon state and to modify database
    private void setDBListeners()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.track_info_add);

        // Create unique url id which is used as identifier to store tracks
        final String url_id = mTrack.getUrl().replaceAll("[./#$\\[\\]]", ",");

        // Set listener on icon
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
                        // Check if url already has an entry in database
                        if(dataSnapshot.getValue() == null)
                        {
                            // If not and not first run, add to database and change icon state
                            if(!firstClick)
                            {
                                childRef.setValue(mTrack);
                                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                            }
                            // If not and first run, change icon state
                            else
                            {
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            firstClick = false;
                        }
                        else
                        {
                            // If it has an entry and not first run, remove from database and change icon state
                            if(!firstClick)
                            {
                                childRef.removeValue();
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            // If it has an entry and first run, change icon state
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
