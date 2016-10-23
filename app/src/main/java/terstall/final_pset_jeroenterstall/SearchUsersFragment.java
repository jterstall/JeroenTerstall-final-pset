package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// This fragment handles the search function for users

public class SearchUsersFragment extends Fragment implements View.OnClickListener
{
    View mView;
    FirebaseDatabase db;
    DatabaseReference ref;
    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.search_users_layout, container, false);

        // Connect to the database and retrieve references with storage position
        connectDB();

        // Set listener on button
        Button search_button = (Button) mView.findViewById(R.id.search_button);
        search_button.setOnClickListener(this);
        return mView;
    }

    // Function to connect to the database and retrieve reference
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
    }

    @Override
    public void onClick(View v)
    {
        // Retrieve user input
        EditText user_query_content = (EditText) mView.findViewById(R.id.user_query);
        final String username = user_query_content.getText().toString();

        // Check if field was actually filled in
        if(username.trim().length() == 0)
        {
            Toast.makeText(getContext(), "Nothing filled in", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ref.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    boolean found = false;
                    // Loop over users
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        // Check if username is the same as user input
                        if(childSnapshot.child(Constants.USERNAME).getValue(String.class).equals(username))
                        {
                            found = true;

                            // If it is, retrieve the email_id and go to the user information page
                            String email_id = childSnapshot.child(Constants.EMAIL).getValue(String.class).replaceAll("[./#$\\[\\]]", ",");
                            activity.goToUserPage(username, email_id, Constants.SEARCH_USER_STACK_INDEX);
                            break;
                        }
                    }
                    if(!found)
                    {
                        Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    // When fragment is attached, retrieve Main Activity
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            activity = (MainActivity) getActivity();
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
    }
}
