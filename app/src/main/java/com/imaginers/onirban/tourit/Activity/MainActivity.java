package com.imaginers.onirban.tourit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.imaginers.onirban.tourit.Fragment.LoginFragment;
import com.imaginers.onirban.tourit.R;


public class MainActivity extends AppCompatActivity {

    private Fragment frag;
    private FirebaseAuth mAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_frag);
      /*  FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/

      mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            this.finish();
            intent=new Intent(getApplicationContext(),SecondActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        }

        else {
            getSupportFragmentManager().beginTransaction().add(R.id.container,new LoginFragment(),"HOME_FRAG").commit();
        }

    }




    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        frag=getSupportFragmentManager().findFragmentByTag("HOME_FRAG");
        if(frag != null && frag.isVisible()) {

            super.onBackPressed();
            this.finish();

        }

        if (count == 0 && frag ==null ) {


            frag=getSupportFragmentManager().findFragmentByTag("REGISTER_FRAG");
            if(frag != null && frag.isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment(),"HOME_FRAG").commit();

            }
            frag=getSupportFragmentManager().findFragmentByTag("HOME_FRAG");
            if(frag != null && frag.isVisible()) {
                super.onBackPressed();
                this.finish();
            }

        }



    }

}
