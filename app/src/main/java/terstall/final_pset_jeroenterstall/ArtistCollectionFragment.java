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

public class ArtistCollectionFragment extends Fragment
{
    ArrayList<Artist> mArtists = new ArrayList<>();
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

        // Clear any previous instances of the arraylist of artists
        mArtists.clear();

        // Retrieve arguments passed
        Bundle args = getArguments();
        email = args.getString(Constants.EMAIL);

        // Get authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Connect to the database and set the correct references
        connectDB();

        // Retrieve artists in user collection
        retrieveArtists();

        return mView;
    }

    // Function to retrieve database instance and set the reference where artists are stored
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.JSON_ARTIST);
    }

    // Function to retrieve all artists from collection from database
    private void retrieveArtists()
    {
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Loop over artists and add to arraylist
                for(DataSnapshot artist : dataSnapshot.getChildren())
                {
                    mArtists.add(artist.getValue(Artist.class));
                }
                // Set adapter and listener of listview with retrieved artists
                setAdapterAndListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // Function to set the adapter and listener of the listview with artists
    private void setAdapterAndListener()
    {
        // Create adapter and set listview with it
        ListView lv = (ListView) mView.findViewById(R.id.list);
        ArtistCollectionAdapter adapter = new ArtistCollectionAdapter(activity, mArtists);
        lv.setAdapter(adapter);

        // Set on click listener in which a click goes to the corresponding artist's page
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Artist mArtist = mArtists.get(position);
                activity.goToArtistInfo(mArtist.getName(), Constants.COLLECTION_STACK_INDEX);
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
