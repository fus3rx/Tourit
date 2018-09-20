package com.imaginers.onirban.tourit.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imaginers.onirban.tourit.Activity.SecondActivity;
import com.imaginers.onirban.tourit.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressDialog progdig;

    private Intent intent;

    private EditText email,pass;
    private Button login;
    private TextView signin;


    public RegisterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rooitView=inflater.inflate(R.layout.register_frag,null);

        mAuth=FirebaseAuth.getInstance();

        progdig=new ProgressDialog(getContext());

        email=(EditText) rooitView.findViewById(R.id.email);
        pass=(EditText) rooitView.findViewById(R.id.pass);
        login=(Button) rooitView.findViewById(R.id.login);
        signin=(TextView) rooitView.findViewById(R.id.signIn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment(),"HOME_FRAG").commit();
            }
        });

        return rooitView;
    }

    private void registerUser() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String singInemail=email.getText().toString().trim();
        String signInpass=pass.getText().toString().trim();
        if (TextUtils.isEmpty(singInemail)) {

            Toast.makeText(getContext(), "Enter email!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (TextUtils.isEmpty(signInpass)) {

            Toast.makeText(getContext(), "Enter pass!", Toast.LENGTH_SHORT).show();
            return;
        }

        progdig.setMessage("Registering please wait...");
        progdig.show();

        mAuth.createUserWithEmailAndPassword(singInemail,signInpass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progdig.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            intent=new Intent(getApplicationContext(),SecondActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0,0);
                        }
                        else {

                            Toast.makeText(getContext(), "Could not registered..Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
