package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

// This fragment handles the search function of tracks, artists and albums

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Spinner spinner;
    private MainActivity activity;
    private EditText query;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.search_layout, container, false);

        // Retrieve views
        button = (Button) mView.findViewById(R.id.search_button);
        query = (EditText) mView.findViewById(R.id.query);
        spinner = (Spinner) mView.findViewById(R.id.search_options_spinner);

        // Create an adapter to populate spinner with
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.search_option, android.R.layout.simple_spinner_dropdown_item);

        // Set adapter and listeners
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        button.setOnClickListener(this);

        // Enter key should perform the search action
        setEnterKeyEditText();
        return mView;
    }

    // Switches hints based on which spinner item is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(position)
        {
            case 0:
                query.setHint("Track name");
                break;
            case 1:
                query.setHint("Artist name");
                break;
            case 2:
                query.setHint("Album name");
                break;
        }
    }

    // Spinner default position
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        query.setHint("Track name");
    }

    // On button click execute the api request for the corresponding search query
    @Override
    public void onClick(View v)
    {
        // Retrieve input from user
        String query_input = query.getText().toString();
        query.setText("");

        // Check if field was not empty
        if(query_input.trim().length() == 0)
        {
            Toast toast = Toast.makeText(activity.getBaseContext(), "Nothing filled in", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            try
            {
                // Encode the input correctly
                query_input = URLEncoder.encode(query_input, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            JSONArray results = null;
            String type = null;
            // Check which of track, artists or album needs to be found
            switch(spinner.getSelectedItemPosition())
            {
                // Tracks
                case 0:
                    try
                    {
                        // Create url and retrieve json from api
                        URL url = new URL(Constants.SEARCH_TRACK_URL + query_input + Constants.API_KEY);
                        JSONObject json = new RetrieveApiInformationTask().execute(url).get();
                        results = (json.getJSONObject(Constants.JSON_RESULT).getJSONObject(Constants.JSON_TRACK_MATCH).getJSONArray(Constants.JSON_TRACK));
                        type = Constants.TRACK_TYPE;
                    }
                    catch (MalformedURLException | InterruptedException | JSONException | ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    break;

                // Artist
                case 1:
                    try
                    {
                        // Create url and retrieve json from api
                        URL url = new URL(Constants.SEARCH_ARTIST_URL + query_input + Constants.API_KEY);
                        JSONObject json = new RetrieveApiInformationTask().execute(url).get();
                        results = json.getJSONObject(Constants.JSON_RESULT).getJSONObject(Constants.JSON_ARTIST_MATCH).getJSONArray(Constants.JSON_ARTIST);
                        type = Constants.ARTIST_TYPE;
                    }
                    catch (MalformedURLException | InterruptedException | ExecutionException | JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;

                // Album
                case 2:
                    try
                    {
                        // Create url and retrieve json from api
                        URL url = new URL(Constants.SEARCH_ALBUM_URL + query_input + Constants.API_KEY);
                        JSONObject json = new RetrieveApiInformationTask().execute(url).get();
                        results = json.getJSONObject(Constants.JSON_RESULT).getJSONObject(Constants.JSON_ALBUM_MATCH).getJSONArray(Constants.JSON_ALBUM);
                        type = Constants.ALBUM_TYPE;
                    }
                    catch (MalformedURLException | InterruptedException | JSONException | ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
            // Hide the keyboard after search button is clicked and input is processed
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            // Go to results page
            activity.goToQueryResults(type, results);
        }
    }

    // Enter key presses the button when the edit text is in focus
    private void setEnterKeyEditText()
    {
        query.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    button.performClick();
                    return true;
                }
                return false;
            }
        });
        query.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        button.performClick();
                        return true;
                    }
                }
                return false;
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
