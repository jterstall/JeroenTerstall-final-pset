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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class ShowAlbumInfoFragment extends Fragment
{

    Album mAlbum;

    boolean firstClick;

    View mView;

    MainActivity activity;

    String album;
    String artist;

    JSONObject album_data;

    FirebaseDatabase db;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_album_info_layout, container, false);
        Bundle args = getArguments();
        artist = args.getString(Constants.JSON_ARTIST);
        album = args.getString(Constants.JSON_ALBUM);
        try
        {
            retrieveAlbumData();
            setAlbumData();
        }
        catch (MalformedURLException | InterruptedException | ExecutionException | JSONException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        connectDB();
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

    private void retrieveAlbumData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException
    {
        artist = URLEncoder.encode(artist, "UTF-8");
        album = URLEncoder.encode(album, "UTF-8");
        URL url = new URL(Constants.GET_ALBUM_URL + "artist=" + artist + "&album=" + album + Constants.API_KEY);
        album_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(Constants.JSON_ALBUM);
    }

    private void setAlbumData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.album_info_artist);
        TextView albumView = (TextView) mView.findViewById(R.id.album_info_title);
        TextView tracksView = (TextView) mView.findViewById(R.id.album_info_tracks);
        TextView summaryView = (TextView) mView.findViewById(R.id.album_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.album_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.album_info_image);

        // Set views with correct values
        String album = (String) album_data.get(Constants.JSON_NAME);
        albumView.setText(album);

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

        // Then set image
        String image_url = (String) album_data.getJSONArray(Constants.JSON_IMAGE).getJSONObject(Constants.JSON_IMAGE_SIZE).get(Constants.JSON_IMAGE_URL);
        if(!image_url.isEmpty())
        {
            Picasso.with(activity).load(image_url).into(imageView);
        }
        else
        {
            imageView.setImageResource(R.drawable.no_image);
        }

        // set the tracks
        String track_content = "";
        JSONArray tracks = album_data.getJSONObject(Constants.JSON_TRACKS).getJSONArray(Constants.JSON_TRACK);
        for(int i = 0; i < tracks.length(); i++)
        {
            track_content = track_content + (i+1) + ": " + tracks.getJSONObject(i).get(Constants.JSON_NAME) + "\n";
        }
        tracksView.setText(track_content);

        // set the tags
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

        String url = (String) album_data.get(Constants.JSON_URL);

        mAlbum = new Album(album, artist, track_content, summary, tags_content, image_url, url);
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.JSON_ALBUM);
    }

    private void setDBListeners()
    {
        final ImageView add = (ImageView) mView.findViewById(R.id.album_info_add);
        final String url_id = mAlbum.getUrl().replaceAll("[./#$\\[\\]]", ",");
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
                                childRef.setValue(mAlbum);
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
