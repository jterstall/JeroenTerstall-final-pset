package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class ShowQueryResultsFragment extends Fragment
{
    MainActivity activity;

    ListView lv;

    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> artistlist= new ArrayList<>();
    ArrayList<String> urllist = new ArrayList<>();

    JSONArray results;

    String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.list_layout, container, false);
        lv = (ListView) mView.findViewById(R.id.list);
        Bundle args = getArguments();
        type = args.getString("type");
        try
        {
            results = new JSONArray(args.getString("results"));
            retrieveQueryResults();
            setQueryList();
        } catch (JSONException | MalformedURLException e)
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

    private void retrieveQueryResults() throws JSONException, MalformedURLException
    {
        for(int i = 0; i < results.length(); i++)
        {
            JSONObject jsonObject = (JSONObject) results.get(i);
            // Get name from JSON
            namelist.add((String) jsonObject.get(Constants.JSON_NAME));
            if(!(type.equals(Constants.ARTIST_TYPE)))
            {
                artistlist.add((String) jsonObject.get(Constants.JSON_ARTIST));
            }
            // Get image from JSON
           urllist.add((String) jsonObject.getJSONArray(Constants.JSON_IMAGE).getJSONObject(Constants.JSON_IMAGE_SIZE).get(Constants.JSON_IMAGE_URL));
        }
    }

    private void setQueryList()
    {
        QueryResultsAdapter adapter = new QueryResultsAdapter(activity, namelist, artistlist, urllist);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    JSONObject jObject = results.getJSONObject(position);
                    String name = (String) jObject.get(Constants.JSON_NAME);
                    if(type.equals(Constants.ARTIST_TYPE))
                    {
                        activity.goToArtistInfo(name, Constants.SEARCH_STACK_INDEX);

                    }
                    else if (type.equals(Constants.TRACK_TYPE))
                    {
                        String artist = (String) jObject.get(Constants.JSON_ARTIST);
                        activity.goToTrackInfo(name, artist, Constants.SEARCH_STACK_INDEX);
                    }
                    else if (type.equals(Constants.ALBUM_TYPE))
                    {
                        String artist = (String) jObject.get(Constants.JSON_ARTIST);
                        activity.goToAlbumInfo(artist, name, Constants.SEARCH_STACK_INDEX);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
