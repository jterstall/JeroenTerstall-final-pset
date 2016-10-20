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

import com.google.firebase.database.ChildEventListener;
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
    private static String api_key = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    private static String track_url = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&";

    Track mTrack;

    View mView;

    JSONObject track_data;

    MainActivity activity;

    String track;
    String artist;

    FirebaseDatabase db;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_track_info_layout, container, false);

        Bundle args = getArguments();
        artist = args.getString("artist");
        track = args.getString("track");

        try
        {
            retrieveArtistData();
            setArtistData();
        }
        catch (MalformedURLException | ExecutionException | JSONException | InterruptedException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        connectDB();
        setAddIconState();
        setAddCollectionListener();
        return mView;
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

    private void retrieveArtistData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        track = URLEncoder.encode(track, "UTF-8");
        artist = URLEncoder.encode(artist, "UTF-8");
        URL url = new URL(track_url + "track=" + track+ "&artist=" + artist + api_key);
        track_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(RetrieveApiInformationTask.JSON_TRACK);
    }

    private void setArtistData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.track_info_artist);
        TextView trackView = (TextView) mView.findViewById(R.id.track_info_title);
        TextView summaryView = (TextView) mView.findViewById(R.id.track_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.track_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.track_info_image);

        // Set views with correct values, first simple one liners
        String image_url = "";
        imageView.setImageResource(R.drawable.no_image);

        String track = (String) track_data.get(RetrieveApiInformationTask.JSON_NAME);
        trackView.setText(track);

        String artist = (String) track_data.getJSONObject(RetrieveApiInformationTask.JSON_ARTIST).get(RetrieveApiInformationTask.JSON_NAME);
        artistView.setText(artist);

        // set the tags
        String tags_content = "";
        JSONArray tags = track_data.getJSONObject(RetrieveApiInformationTask.JSON_TOPTAGS).getJSONArray(RetrieveApiInformationTask.JSON_TAG);
        for(int i = 0; i < tags.length(); i++)
        {
            if(i == 0)
            {
                tags_content = (String) tags.getJSONObject(i).get(RetrieveApiInformationTask.JSON_NAME);
            }
            else
            {
                tags_content = tags_content + ", " + tags.getJSONObject(i).get(RetrieveApiInformationTask.JSON_NAME);
            }
        }
        tagsView.setText(tags_content);

        // Set summary with clickable links
        String summary = (String) track_data.getJSONObject(RetrieveApiInformationTask.JSON_WIKI).get(RetrieveApiInformationTask.JSON_SUMMARY);
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml(summary));

        String url = (String) track_data.get(RetrieveApiInformationTask.JSON_URL);

        // Create Track Object
        mTrack = new Track(track, artist, summary, tags_content, image_url, url);
    }

    private void connectDB()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.track_info_add);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(RetrieveApiInformationTask.JSON_TRACK);
        ref.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {
                System.out.println("MOVED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("CANCELLED");
            }
        });
    }

    private void setAddCollectionListener()
    {
        ImageView add = (ImageView) mView.findViewById(R.id.track_info_add);
        final String url_id = mTrack.getUrl().replaceAll("[./#$\\[\\]]", ",");
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
                        if(dataSnapshot.getValue() == null)
                        {
                            childRef.setValue(mTrack);
                        }
                        else
                        {
                            childRef.removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        System.out.println("CANCELLED");
                    }
                });
            }
        });
    }

    private void setAddIconState()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.track_info_add);
        String url_id = mTrack.getUrl().replaceAll("[./#$\\[\\]]", ",");
        final DatabaseReference childRef = ref.child(url_id);
        childRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() == null)
                {
                    add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                }
                else
                {
                    add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("CANCELLED");
            }
        });
    }
}
