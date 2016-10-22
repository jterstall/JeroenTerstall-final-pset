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
import android.widget.TextView;
import android.widget.Toast;

public class SignInFragment extends Fragment
{
    View mView;
    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.sign_in_layout, container, false);
        Button sign_in_button = (Button) mView.findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText email_content = (EditText) mView.findViewById(R.id.email_sign_in);
                EditText password_content = (EditText) mView.findViewById(R.id.password_sign_in);

                String email = email_content.getText().toString();
                String password = password_content.getText().toString();

                if(email.trim().length() == 0 || password.trim().length() == 0)
                {
                    Toast.makeText(getContext(), "One of the fields was not filled in correctly", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    System.out.println("SIGN IN");
                }
            }
        });
        TextView register_text = (TextView) mView.findViewById(R.id.register_text);
        register_text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                activity.goToRegisterPage();
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
            activity = (MainActivity) context;
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
    }
}
