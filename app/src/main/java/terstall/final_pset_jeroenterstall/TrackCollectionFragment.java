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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Fragment which shows the list of tracks the user has in his/her collection

public class TrackCollectionFragment extends Fragment
{
    ArrayList<Track> mTracks = new ArrayList<>();

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    DatabaseReference ref;

    MainActivity activity;
    View mView;
    String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.list_layout, container, false);

        // Clear any previous instances of the arraylist of tracks
        mTracks.clear();

        // Retrieve passed arguments
        Bundle args = getArguments();
        email = args.getString(Constants.EMAIL);

        // Retrieve user authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Connect to db and set references to storage
        connectDB();

        // Retrieve all tracks in user collection
        retrieveTracks();

        return mView;
    }

    // Function to connect to the database and set references to correct storage
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.JSON_TRACK);
    }

    // Function to retrieve tracks in user collection
    private void retrieveTracks()
    {
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Loop over tracks in database and add to arraylist
                for(DataSnapshot track : dataSnapshot.getChildren())
                {
                    mTracks.add(track.getValue(Track.class));
                }

                // Set listener and adapter on listview
                setAdapterAndListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // Function to populate listview and set listener
    private void setAdapterAndListener()
    {
        ListView lv = (ListView) mView.findViewById(R.id.list);

        // Create custom adapter and set to listview
        TrackCollectionAdapter adapter = new TrackCollectionAdapter(activity, mTracks);
        lv.setAdapter(adapter);

        // Set listener on listview which handles navigation to track information page
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Track mTrack = mTracks.get(position);
                activity.goToTrackInfo(mTrack.getName(), mTrack.getArtist(), Constants.COLLECTION_STACK_INDEX);
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
