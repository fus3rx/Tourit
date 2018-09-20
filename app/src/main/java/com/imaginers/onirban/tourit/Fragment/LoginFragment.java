package com.imaginers.onirban.tourit.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.imaginers.onirban.tourit.Activity.SecondActivity;
import com.imaginers.onirban.tourit.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment {

    private Button emailLogin;
    private RelativeLayout revealLayout,wrapper;
    private boolean check=false;
    private View spacer;
    private TextView signup;

    private EditText email,pass;
    private Button login;
    private LoginButton fsining;

    private CallbackManager mCallbackManager;

    private Intent intent;

    private FirebaseAuth mAuth;
    private ProgressDialog progdig;
    private SignInButton gsignin;


    private final static int RC_SIGN_IN=9000;
    private GoogleApiClient mGoogleApiClient;

    private static Handler handler=new Handler(Looper.getMainLooper());

    public LoginFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.login_frag,null);

        //Initializing views

        emailLogin=(Button) rootView.findViewById(R.id.emailLogin);
        revealLayout=(RelativeLayout) rootView.findViewById(R.id.revealLayout);
        wrapper=(RelativeLayout) rootView.findViewById(R.id.wrapper);
        spacer=(View) rootView.findViewById(R.id.spacer);
        signup=(TextView) rootView.findViewById(R.id.tapSignUp);

        email=(EditText) rootView.findViewById(R.id.email);
        pass=(EditText) rootView.findViewById(R.id.pass);
        login=(Button) rootView.findViewById(R.id.login);

        //google and fb button
        gsignin=(SignInButton) rootView.findViewById(R.id.gSignin);
        fsining=(LoginButton) rootView.findViewById(R.id.fSignin);

        //firebase auth
        mAuth=FirebaseAuth.getInstance();

        //progress dialogue
        progdig=new ProgressDialog(getContext());

        //if already signed in

        //getting firebase token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //creating api client
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mCallbackManager = CallbackManager.Factory.create();
        fsining.setFragment(this);

        fsining.setReadPermissions("email", "public_profile");




        //reveal layout
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRevealLayout();
            }
        });

        //onclick login using email andd pass

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        //google log in

        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GsignIn();
            }
        });

        //facebook log in

        fsining.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                //  Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        //textview onlick

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new RegisterFragment(),"REGISTER_FRAG").commit();
            }
        });

        //setting the text of google sign in button

        TextView textView=(TextView) gsignin.getChildAt(0);
        textView.setText("Login with Google");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        textView.setTextSize(18);


        return rootView;
    }



    //facebook authentication

    private void handleFacebookAccessToken(AccessToken token) {
        // Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //  Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getActivity().finish();
                            intent=new Intent(getActivity(),SecondActivity.class);
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }


    //login using email and pass

    private void userLogin() {
        final InputMethodManager inputManager = (InputMethodManager)
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

        progdig.setMessage("Signing in...");
        progdig.show();

        mAuth.signInWithEmailAndPassword(singInemail,signInpass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progdig.dismiss();
                        if(task.isSuccessful()) {
                            getActivity().finish();

                            intent=new Intent(getApplicationContext(),SecondActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0,0);

                        }
                        else {

                            Toast.makeText(getActivity(), "Could not Login..Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //google log in
    private void GsignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //    Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Authentication successful!.",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            intent=new Intent(getActivity(),SecondActivity.class);
                            startActivity(intent);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //   Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //reveal layout

    private void setRevealLayout() {
        if(check==false) {

            if (Build.VERSION.SDK_INT >= 21) {

                // get the center for the clipping circle
                int cx = revealLayout.getWidth() / 2;
                int cy = 0;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                revealLayout.setVisibility(View.VISIBLE);
                anim.start();
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                p.addRule(RelativeLayout.BELOW, R.id.revealLayout);
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                wrapper.setLayoutParams(p);
            } else {
                revealLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                p.addRule(RelativeLayout.BELOW, R.id.revealLayout);
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                wrapper.setLayoutParams(p);

            }
            check=true;
        }
        else {
            if (Build.VERSION.SDK_INT >= 21) {
                // get the center for the clipping circle
                int cx = revealLayout.getWidth() / 2;
                int cy = 0;

                // get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(cx, cy);

                // create the animation (the final radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, initialRadius, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        revealLayout.setVisibility(View.INVISIBLE);
                    }
                });

                // start the animation
                anim.start();
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                p.addRule(RelativeLayout.BELOW, R.id.spacer);
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                wrapper.setLayoutParams(p);

            }

            else {
                revealLayout.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                p.addRule(RelativeLayout.BELOW, R.id.spacer);
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                wrapper.setLayoutParams(p);

            }
            check = false;
        }
    }


    @Override
    public void onDestroy() {
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        progdig.dismiss();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        progdig.dismiss();
        super.onPause();
    }
}
