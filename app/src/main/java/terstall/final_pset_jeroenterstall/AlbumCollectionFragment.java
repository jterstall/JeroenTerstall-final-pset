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

public class AlbumCollectionFragment extends Fragment
{
    ArrayList<Album> mAlbums = new ArrayList<>();
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    MainActivity activity;
    ListView lv;
    String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.list_layout, container, false);
        Bundle args = getArguments();
        email = args.getString(Constants.EMAIL);
        mAuth = FirebaseAuth.getInstance();
        lv = (ListView) mView.findViewById(R.id.list);
        mAlbums.clear();
        connectDB();
        retrieveAlbums();
        return mView;
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.JSON_ALBUM);
    }

    private void retrieveAlbums()
    {
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot album : dataSnapshot.getChildren())
                {
                    mAlbums.add(album.getValue(Album.class));
                }
                setAdapterAndListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setAdapterAndListener()
    {
        AlbumCollectionAdapter adapter = new AlbumCollectionAdapter(activity, mAlbums);
        lv.setAdapter(adapter);
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
