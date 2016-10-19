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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class ShowAlbumInfoFragment extends Fragment
{
    private static String api_key = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    private static String album_url = "http://ws.audioscrobbler.com/2.0/?method=album.getInfo&";

    View mView;

    MainActivity activity;

    String type;
    String album;
    String artist;

    JSONObject album_data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.show_album_info_layout, container, false);
        Bundle args = getArguments();
        type = args.getString("type");
        artist = args.getString("artist");
        album = args.getString("album");
        try
        {
            retrieveAlbumData();
            setAlbumData();
        }
        catch (MalformedURLException | InterruptedException | ExecutionException | JSONException e)
        {
            e.printStackTrace();
        }
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

    private void retrieveAlbumData() throws MalformedURLException, ExecutionException, InterruptedException, JSONException
    {
        URL url = new URL(album_url + "artist=" + artist.replaceAll(" ", "+") + "&album=" + album.replaceAll(" ", "+") + api_key);
        album_data = new RetrieveApiInformationTask().execute(url).get().getJSONObject(RetrieveApiInformationTask.JSON_ALBUM);
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

        // Set views with correct values, first simple one liners
        albumView.setText((String) album_data.get(RetrieveApiInformationTask.JSON_NAME));
        artistView.setText((String) album_data.get(RetrieveApiInformationTask.JSON_ARTIST));

        //TODO release date
        // Set summary with clickable links
        summaryView.setClickable(true);
        summaryView.setMovementMethod(LinkMovementMethod.getInstance());
        summaryView.setText(Html.fromHtml((String) album_data.getJSONObject(RetrieveApiInformationTask.JSON_WIKI).get(RetrieveApiInformationTask.JSON_SUMMARY)));

        // Then set image
        String image_url = (String) album_data.getJSONArray(RetrieveApiInformationTask.JSON_IMAGE).getJSONObject(RetrieveApiInformationTask.JSON_IMAGE_SIZE).get(RetrieveApiInformationTask.JSON_IMAGE_URL);
        Picasso.with(activity).load(image_url).into(imageView);

        // set the tracks
        String track_content = "";
        JSONArray tracks = album_data.getJSONObject(RetrieveApiInformationTask.JSON_TRACKS).getJSONArray(RetrieveApiInformationTask.JSON_TRACK);
        for(int i = 0; i < tracks.length(); i++)
        {
            track_content = track_content + (i+1) + ": " + tracks.getJSONObject(i).get(RetrieveApiInformationTask.JSON_NAME) + "\n";
        }
        tracksView.setText(track_content);

        // set the tags
        String tags_content = "";
        JSONArray tags = album_data.getJSONObject(RetrieveApiInformationTask.JSON_TAGS).getJSONArray(RetrieveApiInformationTask.JSON_TAG);
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
}
