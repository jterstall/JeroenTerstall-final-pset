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
    ArrayList<User> mUsers = new ArrayList<>();

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

        // Clear any previous instantiations of users arraylist
        mUsers.clear();

        // Get authentication instance and email of currently logged in user
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail().replaceAll("[./#$\\[\\]]", ",");

        // Connect to the database and set references to storage
        connectDB();

        // Retrieve all users followed
        retrieveUsers();

        return mView;
    }

    // Function to connect to the database and set references to storage
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
        ref = ref.child(email);
        ref = ref.child(Constants.FOLLOWED_USERS);
    }

    // Function to retrieve all users from the database
    private void retrieveUsers()
    {
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Loop over users and add to arraylist
                for(DataSnapshot user : dataSnapshot.getChildren())
                {
                    mUsers.add(user.getValue(User.class));
                }

                // Populate listview and set listener
                setAdapterAndListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // Function to populate listview and set listeners
    private void setAdapterAndListener()
    {
        // Retrieve only the usernames from user objects
        ArrayList<String> usernames = new ArrayList<>();
        for(User user: mUsers)
        {
            usernames.add(user.getUsername());
        }

        // Create adapter and set listview with it
        ListView lv = (ListView) mView.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(), android.R.layout.simple_selectable_list_item, android.R.id.text1, usernames);
        lv.setAdapter(adapter);

        // Set listener, which handles navigation to user information page
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
