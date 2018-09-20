package com.imaginers.onirban.tourit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.imaginers.onirban.tourit.Fragment.EventFragment;
import com.imaginers.onirban.tourit.Fragment.GalleryFragment;
import com.imaginers.onirban.tourit.Fragment.HomeFragment;
import com.imaginers.onirban.tourit.Fragment.NearbyFragment;
import com.imaginers.onirban.tourit.R;


public class SecondActivity extends AppCompatActivity {

    private Button logout;
    private Intent intent;
    private TextView userName;

    private FirebaseAuth mAuth;

    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    private BottomNavigationView mBottombar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);


        mBottombar=(BottomNavigationView) findViewById(R.id.navigation);

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.motherView,new HomeFragment()).commit();
        }

        mBottombar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                fragmentManager=getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                String tag;
                /*checking the ids to server correct fragment transition*/
                if(item.getItemId() == R.id.menu_nearby){
                    fragment = new NearbyFragment();
                    tag="NearbyFragment";
                    item.setChecked(true);
                }else if(item.getItemId() == R.id.menu_events){
                    fragment=new EventFragment();
                    tag="event_frag";
                    item.setChecked(true);
                }else if(item.getItemId() == R.id.menu_camera){
                    fragment=new GalleryFragment();
                    tag="camera_frag";
                    item.setChecked(true);
                }else {
                    fragment = new HomeFragment();
                    tag="HomeFragment";
                    item.setChecked(true);
                }

                updateToolbarText(item.getTitle());
                fragmentTransaction.replace(R.id.motherView,fragment,tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                return false;
            }
        });
    }

    /*changing the title of the toolbar according to frags*/
    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


}
