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

// This fragment shows the information of an album

public class ShowAlbumInfoFragment extends Fragment
{
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference ref;

    View mView;
    MainActivity activity;

    Album mAlbum;
    JSONObject album_data;

    boolean firstClick;
    String album;
    String artist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_album_info_layout, container, false);

        // Retrieve authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Retrieve passed arguments
        Bundle args = getArguments();
        artist = args.getString(Constants.JSON_ARTIST);
        album = args.getString(Constants.JSON_ALBUM);

        try
        {
            // Retrieve album data and populate layout with it
            retrieveAlbumData();
            setAlbumData();
        }
        catch (MalformedURLException | InterruptedException | ExecutionException | JSONException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        // Connect to the database and set correct storage references
        connectDB();

        // Boolean needed to set icon state of the playlist add icon
        firstClick = true;

        // Set listeners
        setDBListeners();

        return mView;
    }

    // Retrieve album data by calling the API
    private void retrieveAlbumData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        // Encode input correctly
        artist = URLEncoder.encode(artist, "UTF-8");
        album = URLEncoder.encode(album, "UTF-8");
        URL url = new URL(Constants.GET_ALBUM_URL + "artist=" + artist + "&album=" + album + Constants.API_KEY);
        album_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(Constants.JSON_ALBUM);
    }

    // This functions sets all views with the correct data from the api
    private void setAlbumData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.album_info_artist);
        TextView albumView = (TextView) mView.findViewById(R.id.album_info_title);
        TextView tracksView = (TextView) mView.findViewById(R.id.album_info_tracks);
        TextView summaryView = (TextView) mView.findViewById(R.id.album_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.album_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.album_info_image);

        // Set album name
        String album = (String) album_data.get(Constants.JSON_NAME);
        albumView.setText(album);

        // Set artist name
        String artist = (String) album_data.get(Constants.JSON_ARTIST);
        artistView.setText(artist);

        // Set summary with clickable links
        String summary = "";
        if(album_data.has(Constants.JSON_WIKI))
        {
            summary = (String) album_data.getJSONObject(Constants.JSON_WIKI).get(Constants.JSON_SUMMARY);
        }
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml(summary));

        // Set album cover
        String image_url = (String) album_data.getJSONArray(Constants.JSON_IMAGE).getJSONObject(Constants.JSON_IMAGE_SIZE).get(Constants.JSON_IMAGE_URL);
        if(!image_url.isEmpty())
        {
            Picasso.with(activity).load(image_url).into(imageView);
        }
        else
        {
            imageView.setImageResource(R.drawable.no_image);
        }

        // Set the tracks on album
        String track_content = "";
        JSONArray tracks = album_data.getJSONObject(Constants.JSON_TRACKS).getJSONArray(Constants.JSON_TRACK);
        for(int i = 0; i < tracks.length(); i++)
        {
            track_content = track_content + (i+1) + ": " + tracks.getJSONObject(i).get(Constants.JSON_NAME) + "\n";
        }
        tracksView.setText(track_content);

        // Set the tags
        String tags_content = "";
        JSONArray tags = album_data.getJSONObject(Constants.JSON_TAGS).getJSONArray(Constants.JSON_TAG);
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

        // Retrieve url of album on lastfm website, which is used as unique id to store in database
        String url = (String) album_data.get(Constants.JSON_URL);

        // Create album object from retrieved data
        mAlbum = new Album(album, artist, track_content, summary, tags_content, image_url, url);
    }

    // Connect to the database and set references to correct storage place
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ","));
        ref = ref.child(Constants.JSON_ALBUM);
    }

    // Set listeners to check for add playlist actions
    private void setDBListeners()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.album_info_add);

        // Retrieve unique url id and replace all illegal characters with comma
        final String url_id = mAlbum.getUrl().replaceAll("[./#$\\[\\]]", ",");

        // Set listener on add button
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Set reference to unique id
                final DatabaseReference childRef = ref.child(url_id);
                childRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Check if album exists in database
                        if(dataSnapshot.getValue() == null)
                        {
                            // If not and not the first run, set album in database and change icon
                            if(!firstClick)
                            {
                                childRef.setValue(mAlbum);
                                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                            }
                            // If not and first run, set add icon state correctly
                            else
                            {
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            firstClick = false;
                        }
                        else
                        {
                            // If exists and not first run, remove album from database and change icon
                            if(!firstClick)
                            {
                                childRef.removeValue();
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            // If exists and first run, set add icon correctly
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
        // Perform a click to set add icon state correctly
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
