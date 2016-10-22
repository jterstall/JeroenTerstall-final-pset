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

    Track mTrack;

    boolean firstClick;

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
        artist = args.getString(Constants.JSON_ARTIST);
        track = args.getString(Constants.JSON_TRACK);

        try
        {
            retrieveTrackData();
            setTrackData();
        }
        catch (MalformedURLException | ExecutionException | JSONException | InterruptedException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        connectDB();

        // Flag for setting image resource on first run
        firstClick = true;

        setDBListeners();
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

    private void retrieveTrackData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        track = URLEncoder.encode(track, "UTF-8");
        artist = URLEncoder.encode(artist, "UTF-8");
        URL url = new URL(Constants.GET_TRACK_URL + "track=" + track+ "&artist=" + artist + Constants.API_KEY);
        track_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(Constants.JSON_TRACK);
    }

    private void setTrackData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.track_info_artist);
        TextView trackView = (TextView) mView.findViewById(R.id.track_info_title);
        TextView summaryView = (TextView) mView.findViewById(R.id.track_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.track_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.track_info_image);

        // Set views with correct values, first simple one liners
        imageView.setImageResource(R.drawable.no_image);

        String track = (String) track_data.get(Constants.JSON_NAME);
        trackView.setText(track);

        String artist = (String) track_data.getJSONObject(Constants.JSON_ARTIST).get(Constants.JSON_NAME);
        artistView.setText(artist);

        // set the tags
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

        String url = (String) track_data.get(Constants.JSON_URL);

        // Create Track Object
        mTrack = new Track(track, artist, summary, tags_content, url);
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.JSON_TRACK);
    }

    private void setDBListeners()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.track_info_add);
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
                            if(!firstClick)
                            {
                                childRef.setValue(mTrack);
                                add.setImageResource(R.drawable.ic_playlist_add_check_white_18dp);
                            }
                            else
                            {
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
                            firstClick = false;
                        }
                        else
                        {
                            if(!firstClick)
                            {
                                childRef.removeValue();
                                add.setImageResource(R.drawable.ic_playlist_add_white_18dp);
                            }
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
                        System.out.println("CANCELLED");
                    }
                });
            }
        });
        add.performClick();
    }
}
