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
        View mView = inflater.inflate(R.layout.show_query_results_layout, container, false);
        lv = (ListView) mView.findViewById(R.id.query_list);
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
            namelist.add((String) jsonObject.get(RetrieveApiInformationTask.JSON_NAME));
            if(!(type.equals(MainActivity.ARTIST_TYPE)))
            {
                artistlist.add((String) jsonObject.get(RetrieveApiInformationTask.JSON_ARTIST));
            }
            // Get image from JSON
           urllist.add((String) jsonObject.getJSONArray(RetrieveApiInformationTask.JSON_IMAGE).getJSONObject(RetrieveApiInformationTask.JSON_IMAGE_SIZE).get(RetrieveApiInformationTask.JSON_IMAGE_URL));
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
                    JSONObject jObject = (JSONObject) results.getJSONObject(position);
                    activity.goToMusicInfo(type, jObject);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
