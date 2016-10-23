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
        connectDB();
        Button register_button = (Button) mView.findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
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

    @Override
    public void onClick(View v)
    {
        EditText username_content = (EditText) mView.findViewById(R.id.username_register);
        EditText email_content = (EditText) mView.findViewById(R.id.email_register);
        EditText password_content = (EditText) mView.findViewById(R.id.password_register);
        EditText password_check_content = (EditText) mView.findViewById(R.id.password_check);

        String username = username_content.getText().toString();
        String email = email_content.getText().toString();
        String password = password_content.getText().toString();
        String password_check = password_check_content.getText().toString();

        if(username.trim().length() == 0 || email.trim().length() == 0 || password.trim().length() == 0 || password_check.trim().length() == 0)
        {
            Toast.makeText(getContext(), "One of the fields was not filled in correctly", Toast.LENGTH_SHORT).show();
        }
        else if (!(password.equals(password_check)))
        {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            checkUserNameExists(email, password, username);
        }
    }

    private void createUser(final String email, final String password, final String username)
    {
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
                    final DatabaseReference childRef = ref.child(email.replaceAll("[./#$\\[\\]]", ","));
                    User mUser = new User(email, username);
                    childRef.setValue(mUser);
                }
            }
        });
    }

    private void connectDB()
    {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(Constants.USERS);
    }

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
}
