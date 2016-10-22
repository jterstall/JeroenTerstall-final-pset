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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackCollectionFragment extends Fragment
{
    ArrayList<Track> mTracks = new ArrayList<>();
    FirebaseDatabase db;
    DatabaseReference ref;
    MainActivity activity;
    ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View mView = inflater.inflate(R.layout.list_layout, container, false);
        lv = (ListView) mView.findViewById(R.id.list);
        mTracks.clear();
        connectDB();
        retrieveTracks();
        return mView;
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(RetrieveApiInformationTask.JSON_TRACK);
    }

    private void retrieveTracks()
    {
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot track : dataSnapshot.getChildren())
                {
                    mTracks.add(track.getValue(Track.class));
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
        TrackCollectionAdapter adapter = new TrackCollectionAdapter(activity, mTracks);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Track mTrack = mTracks.get(position);
                activity.goToTrackInfo(mTrack.getName(), mTrack.getArtist(), activity.COLLECTION_STACK_INDEX);
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
