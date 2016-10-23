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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserCollectionFragment extends Fragment
{
    View mView;
    ArrayList<User> mUsers = new ArrayList<>();
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
        mView = inflater.inflate(R.layout.list_layout, container, false);

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ",");
        lv = (ListView) mView.findViewById(R.id.list);
        mUsers.clear();
        connectDB();
        retrieveUsers();
        return mView;
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.FOLLOWED_USERS);
    }

    private void retrieveUsers()
    {
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot user : dataSnapshot.getChildren())
                {
                    mUsers.add(user.getValue(User.class));
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
        ArrayList<String> usernames = new ArrayList<>();
        for(User user: mUsers)
        {
            usernames.add(user.getUsername());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(), android.R.layout.simple_selectable_list_item, android.R.id.text1, usernames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                User mUser = mUsers.get(position);
                activity.goToUserPage(mUser.getUsername(), mUser.getEmail(), Constants.USER_COLLECTION_STACK_INDEX);
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
