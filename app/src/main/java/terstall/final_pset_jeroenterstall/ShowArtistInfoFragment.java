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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;


public class ShowArtistInfoFragment extends Fragment
{
    private static String api_key = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    private static String artist_url = "http://ws.audioscrobbler.com/2.0/?method=artist.getInfo&artist=";

    View mView;

    JSONObject artist_data;

    MainActivity activity;

    String type;
    String artist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_artist_info_layout, container, false);
        Bundle args = getArguments();
        artist = args.getString("artist");
        try
        {
            retrieveArtistData();
            setArtistData();
        }
        catch (MalformedURLException | ExecutionException | InterruptedException | JSONException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
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
        artist = URLEncoder.encode(artist, "UTF-8");
        URL url = new URL(artist_url + artist + api_key);
        artist_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(RetrieveApiInformationTask.JSON_ARTIST);
    }

    private void setArtistData() throws JSONException
    {
        // First retrieve all views
        TextView artistView = (TextView) mView.findViewById(R.id.artist_info_title);
        TextView summaryView = (TextView) mView.findViewById(R.id.artist_info_summary);
        TextView tagsView = (TextView) mView.findViewById(R.id.artist_info_tags);
        ImageView imageView = (ImageView) mView.findViewById(R.id.artist_info_image);

        // Set views with correct values, first simple one liners
        artistView.setText((String) artist_data.get(RetrieveApiInformationTask.JSON_NAME));

        // Set summary with clickable links
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml((String) artist_data.getJSONObject(RetrieveApiInformationTask.JSON_BIO).get(RetrieveApiInformationTask.JSON_SUMMARY)));

        // Then set image
        String image_url = (String) artist_data.getJSONArray(RetrieveApiInformationTask.JSON_IMAGE).getJSONObject(RetrieveApiInformationTask.JSON_IMAGE_SIZE).get(RetrieveApiInformationTask.JSON_IMAGE_URL);
        if(!image_url.isEmpty())
        {
            Picasso.with(activity).load(image_url).into(imageView);
        }
        else
        {
            imageView.setImageResource(R.drawable.no_image);
        }

        // set the tags
        String tags_content = "";
        JSONArray tags = artist_data.getJSONObject(RetrieveApiInformationTask.JSON_TAGS).getJSONArray(RetrieveApiInformationTask.JSON_TAG);
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

    }

    private void setAddCollectionListener()
    {
        ImageView add = (ImageView) mView.findViewById(R.id.artist_info_add);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addToCollection();
            }
        });
    }

    private void addToCollection()
    {
        System.out.println("CLICK");
    }
}
