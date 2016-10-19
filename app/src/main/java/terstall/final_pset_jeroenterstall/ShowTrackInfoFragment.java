package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.MalformedURLException;
import java.net.URL;


public class ShowTrackInfoFragment extends Fragment
{
    private static String api_key = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    private static String track_url = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&";

    MainActivity activity;

    String type;
    String track;
    String artist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.show_track_info_layout, container, false);
        Bundle args = getArguments();
        type = args.getString("type");
        artist = args.getString("artist");
        track = args.getString("track");
        try
        {
            retrieveJSON();
        }
        catch (MalformedURLException e)
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

    private void retrieveJSON() throws MalformedURLException
    {
        URL url = new URL(track_url + "track=" + track.replaceAll(" ", "+") + "&artist=" + artist.replaceAll(" ", "+") + api_key);
        System.out.println(url);
    }
}
