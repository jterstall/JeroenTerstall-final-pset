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

// Fragment to show the list of albums in the users collection

public class AlbumCollectionFragment extends Fragment
{
    ArrayList<Album> mAlbums = new ArrayList<>();
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

        // Clear any previously filled arraylists
        mAlbums.clear();

        // Retrieve passed arguments
        Bundle args = getArguments();
        email = args.getString(Constants.EMAIL);

        // Get current authentication instance from firebase
        mAuth = FirebaseAuth.getInstance();

        // Connect to the database and retrieve correct references
        connectDB();

        // Retrieve albums in collection
        retrieveAlbums();
        return mView;
    }

    // Function to connect to the database and retrieve the correct references to the place albums are stored
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.JSON_ALBUM);
    }

    // From database references, retrieve all albums stored
    private void retrieveAlbums()
    {
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Loop over albums and add to the arraylist
                for(DataSnapshot album : dataSnapshot.getChildren())
                {
                    mAlbums.add(album.getValue(Album.class));
                }
                // Set the adapter and listener to the listview with retrieved collection of albums
                setAdapterAndListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // Function to set the listviews adapter and listener
    private void setAdapterAndListener()
    {
        // Retrieve listview, create adapter and set it
        ListView lv = (ListView) mView.findViewById(R.id.list);
        AlbumCollectionAdapter adapter = new AlbumCollectionAdapter(activity, mAlbums);
        lv.setAdapter(adapter);

        // Listen for clicks to go to the album info page of the album clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Album mAlbum = mAlbums.get(position);
                activity.goToAlbumInfo(mAlbum.getArtist(), mAlbum.getName(), Constants.COLLECTION_STACK_INDEX);
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
