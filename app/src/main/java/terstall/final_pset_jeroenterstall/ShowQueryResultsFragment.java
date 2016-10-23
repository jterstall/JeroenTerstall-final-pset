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

// This fragment shows the results from the search query

public class ShowQueryResultsFragment extends Fragment
{
    MainActivity activity;
    View mView;

    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> artistlist= new ArrayList<>();
    ArrayList<String> urllist = new ArrayList<>();

    JSONArray results;

    String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.list_layout, container, false);

        // Retrieve passed arguments
        Bundle args = getArguments();
        type = args.getString("type");

        try
        {
            // Retrieve json results from passed data
            results = new JSONArray(args.getString("results"));
            // Retrieve all possible results from JSON array and set list with it
            retrieveQueryResults();
            setQueryList();
        }
        catch (JSONException | MalformedURLException e)
        {
            e.printStackTrace();
        }

        return mView;
    }

    // This function retrieves all names, urls and artists resulting from api request
    private void retrieveQueryResults() throws JSONException, MalformedURLException
    {
        // Loop over json array
        for(int i = 0; i < results.length(); i++)
        {
            // Get json object of current iteration
            JSONObject jsonObject = (JSONObject) results.get(i);

            // Get name from JSON and add to arraylist
            namelist.add((String) jsonObject.get(Constants.JSON_NAME));

            // Get artist if not artist, because artist doesnt have an artist field and add to arraylist
            if(!(type.equals(Constants.ARTIST_TYPE)))
            {
                artistlist.add((String) jsonObject.get(Constants.JSON_ARTIST));
            }

            // Get image from JSON and add to arraylist
           urllist.add((String) jsonObject.getJSONArray(Constants.JSON_IMAGE).getJSONObject(Constants.JSON_IMAGE_SIZE).get(Constants.JSON_IMAGE_URL));
        }
    }

    // Set listview with previously retrieved query results
    private void setQueryList()
    {
        ListView lv = (ListView) mView.findViewById(R.id.list);

        // Create custom adapter and set to listview
        QueryResultsAdapter adapter = new QueryResultsAdapter(activity, namelist, artistlist, urllist);
        lv.setAdapter(adapter);

        // Listen for clicks to go to corresponding information pages
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    // Retrieve json object
                    JSONObject jObject = results.getJSONObject(position);

                    // Retrieve name
                    String name = (String) jObject.get(Constants.JSON_NAME);

                    // If statements to check which type it is and to go to corresponding information page
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
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
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
