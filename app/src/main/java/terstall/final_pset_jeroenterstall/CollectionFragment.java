package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

// Fragment in which the user can select to go to his/her artists, tracks or albums

public class CollectionFragment extends Fragment
{
    MainActivity activity;
    String[] COLLECTION_VALUES = new String[] {"Your Tracks", "Your Artists", "Your Albums"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.collection_layout, container, false);

        ListView lv = (ListView) mView.findViewById(R.id.collection_list);

        // Retrieve current user email which is the identifier of the user
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ",");

        // Set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_selectable_list_item, android.R.id.text1, COLLECTION_VALUES);
        lv.setAdapter(adapter);

        // Set on click listener to go to the correct collection page of tracks, artists or albums
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        activity.goToTrackCollection(email, Constants.COLLECTION_STACK_INDEX);
                        break;
                    case 1:
                        activity.goToArtistCollection(email, Constants.COLLECTION_STACK_INDEX);
                        break;
                    case 2:
                        activity.goToAlbumCollection(email, Constants.COLLECTION_STACK_INDEX);
                        break;
                }
            }
        });

        return mView;
    }

    // Retrieve activity which called the fragment
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
