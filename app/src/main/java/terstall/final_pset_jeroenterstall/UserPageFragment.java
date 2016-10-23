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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPageFragment extends Fragment
{
    MainActivity activity;
    View mView;
    String[] COLLECTION_VALUES = new String[] {"Tracks", "Artists", "Albums"};

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    DatabaseReference ref;

    boolean firstClick;

    User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.user_page_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        Bundle args = getArguments();
        String username = args.getString(Constants.USERNAME);
        final String email = args.getString(Constants.EMAIL);
        final int index = args.getInt(Constants.INDEX);
        TextView user_page_title = (TextView) mView.findViewById(R.id.username);
        user_page_title.setText(username);

        mUser = new User(email, username);

        firstClick = true;

        connectDB();
        setDBListeners();

        ListView lv = (ListView) mView.findViewById(R.id.collection_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(), android.R.layout.simple_selectable_list_item, android.R.id.text1, COLLECTION_VALUES);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        activity.goToTrackCollection(email, index);
                        break;
                    case 1:
                        activity.goToArtistCollection(email, index);
                        break;
                    case 2:
                        activity.goToAlbumCollection(email, index);
                        break;
                }
            }
        });

        return mView;
    }

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

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ","));
        ref = ref.child(Constants.FOLLOWED_USERS);
    }

    public void setDBListeners()
    {
        final ImageView follow_button = (ImageView) mView.findViewById(R.id.follow_button);
        follow_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DatabaseReference childRef = ref.child(mUser.getEmail());
                childRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.getValue() == null)
                        {
                            if(!firstClick)
                            {
                                childRef.setValue(mUser);
                                follow_button.setImageResource(R.drawable.ic_favorite_black_24dp);
                            }
                            else
                            {
                                follow_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            }
                            firstClick = false;
                        }
                        else
                        {
                            if(!firstClick)
                            {
                                childRef.removeValue();
                                follow_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            }
                            else
                            {
                                follow_button.setImageResource(R.drawable.ic_favorite_black_24dp);
                            }
                            firstClick = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });
        follow_button.performClick();
    }
}
