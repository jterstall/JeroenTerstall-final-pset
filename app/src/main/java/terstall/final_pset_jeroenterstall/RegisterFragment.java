package terstall.final_pset_jeroenterstall;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// This fragment handles the registering of users

public class RegisterFragment extends Fragment implements View.OnClickListener
{
    View mView;
    MainActivity activity;

    FirebaseDatabase db;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.register_layout, container, false);

        // Connect to the database and retrieve correct reference to storage of users
        connectDB();

        // Set listener on register button
        Button register_button = (Button) mView.findViewById(R.id.register_button);
        register_button.setOnClickListener(this);

        return mView;
    }

    // This function retrieves the database instance and sets the correct references to the storage
    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
    }

    @Override
    public void onClick(View v)
    {
        // Retrieve values filled in and put in String variables
        EditText username_content = (EditText) mView.findViewById(R.id.username_register);
        EditText email_content = (EditText) mView.findViewById(R.id.email_register);
        EditText password_content = (EditText) mView.findViewById(R.id.password_register);
        EditText password_check_content = (EditText) mView.findViewById(R.id.password_check);

        String username = username_content.getText().toString();
        String email = email_content.getText().toString();
        String password = password_content.getText().toString();
        String password_check = password_check_content.getText().toString();

        // Check if all fields are filled in
        if(username.trim().length() == 0 || email.trim().length() == 0 || password.trim().length() == 0 || password_check.trim().length() == 0)
        {
            Toast.makeText(getContext(), "One of the fields was not filled in correctly", Toast.LENGTH_SHORT).show();
        }
        // Check if both password fields are the same
        else if (!(password.equals(password_check)))
        {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        // If the input goes through the checks, check if the user exists
        else
        {
            checkUserNameExists(email, password, username);
        }
    }

    // This function checks if the username provided already exists, if not calls create user function
    private void checkUserNameExists(final String email, final String password, final String username)
    {
        final DatabaseReference childRef = ref.child(username);
        childRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() == null)
                {
                    createUser(email, password, username);
                }
                else
                {
                    Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    // This function creates the user with provided information
    private void createUser(final String email, final String password, final String username)
    {
        // Call built-in create user function and check when it is completed
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(!task.isSuccessful())
                {
                    Toast.makeText(activity, "Register Failed", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // If user was created succesfully, also store the user in the database
                    final DatabaseReference childRef = ref.child(email.replaceAll("[./#$\\[\\]]", ","));
                    User mUser = new User(email, username);
                    childRef.setValue(mUser);
                }
            }
        });
    }

    // When fragment is attached, retrieve main activity
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
