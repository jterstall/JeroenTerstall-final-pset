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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Spinner spinner;
    private MainActivity activity;
    private EditText query;
    private Button button;

    private String api_key = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    private String artist_url = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=";
    private String album_url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album=";
    private String track_url = "http://ws.audioscrobbler.com/2.0/?method=track.search&track=";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.search_layout, container, false);
        button = (Button) mView.findViewById(R.id.search_button);
        query = (EditText) mView.findViewById(R.id.query);
        spinner = (Spinner) mView.findViewById(R.id.search_options_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.search_option, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        button.setOnClickListener(this);
        setEnterKeyEditText();
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
            System.out.println(e);
        }
    }

    // Change edit text hints based on spinner selected item
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
        String query_input = query.getText().toString();
        query.setText("");
        if(query_input.trim().length() == 0)
        {
            Toast toast = Toast.makeText(activity.getBaseContext(), "Nothing filled in", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            query_input = query_input.replaceAll(" ", "+");
            switch(spinner.getSelectedItemPosition())
            {
                // Track name
                case 0:
                    System.out.println(track_url + query_input + api_key);
                    break;
                // Artist
                case 1:
                    System.out.println(artist_url + query_input + api_key);
                    break;
                // Album
                case 2:
                    System.out.println(album_url + query_input + api_key);
                    break;
            }
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

}
