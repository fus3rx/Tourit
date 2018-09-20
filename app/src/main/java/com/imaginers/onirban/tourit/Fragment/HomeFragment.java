package com.imaginers.onirban.tourit.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginers.onirban.tourit.Activity.SettingsActivity;
import com.imaginers.onirban.tourit.Class.EventDatabase;
import com.imaginers.onirban.tourit.Class.EventDatabaseOpenHelper;
import com.imaginers.onirban.tourit.Class.EventModel;
import com.imaginers.onirban.tourit.R;
import com.imaginers.onirban.tourit.data.Channel;
import com.imaginers.onirban.tourit.data.Condition;
import com.imaginers.onirban.tourit.data.LocationResult;
import com.imaginers.onirban.tourit.data.Units;
import com.imaginers.onirban.tourit.listener.GeocodingServiceListener;
import com.imaginers.onirban.tourit.listener.WeatherServiceListener;
import com.imaginers.onirban.tourit.service.GoogleMapsGeocodingService;
import com.imaginers.onirban.tourit.service.WeatherCacheService;
import com.imaginers.onirban.tourit.service.YahooWeatherService;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements WeatherServiceListener,GeocodingServiceListener,LocationListener {

    private RecyclerView recyclerView;
    private String eventName,eventDesti,eventDate,eventTime,eventDes,eventBudget,delId;
    private ArrayList<EventModel> eventList=new ArrayList<>();

    //Weather app property

    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private YahooWeatherService weatherService;
    private GoogleMapsGeocodingService geocodingService;
    private WeatherCacheService cacheService;

   // private ProgressDialog loadingDialog;

    // weather service fail flag
    private boolean weatherServicesHasFailed = false;

    private SharedPreferences preferences = null;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        initializeList();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_frag,container, false);

        recyclerView=(RecyclerView) rootView.findViewById(R.id.cardViewForHome);


        //Setting the Ui parameters of the Recycle View
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if(eventList.size()>0 && recyclerView != null) {
            //Setting the Adapter of the recycle view
            recyclerView.setAdapter(new MyAdapter(eventList));
        }

        recyclerView.setLayoutManager(layoutManager);

        //Weather layout property

        weatherIconImageView = (ImageView) rootView.findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) rootView.findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) rootView.findViewById(R.id.conditionTextView);
        locationTextView = (TextView) rootView.findViewById(R.id.locationTextView);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        weatherService = new YahooWeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(getString(R.string.pref_temperature_unit), null));

        geocodingService = new GoogleMapsGeocodingService(this);
        cacheService = new WeatherCacheService(getContext());

      /*  if (preferences.getBoolean(getString(R.string.pref_needs_setup), true)) {
            startSettingsActivity();
        }*/


        return rootView;


    }

    //weather class method starts from here

    @Override
    public void onStart() {
        super.onStart();

        /*loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.setCancelable(false);
        loadingDialog.dismiss();
        loadingDialog.show();*/

        String location = null;

        if (preferences.getBoolean(getString(R.string.pref_geolocation_enabled), true)) {
            String locationCache = preferences.getString(getString(R.string.pref_cached_location), null);

            if (locationCache == null) {
                getWeatherFromCurrentLocation();
            } else {
                location = locationCache;
            }
        } else {
            location = preferences.getString(getString(R.string.pref_manual_location), null);
        }

        if (location != null) {
            weatherService.refreshWeather(location);
        }

    }

    private void getWeatherFromCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, GET_WEATHER_FROM_CURRENT_LOCATION);

            return;
        }

        // system's LocationManager
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }

        locationManager.requestSingleUpdate(locationCriteria, this, null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == HomeFragment.GET_WEATHER_FROM_CURRENT_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherFromCurrentLocation();
            } else {
              //  loadingDialog.dismiss();

                AlertDialog messageDialog = new AlertDialog.Builder(getContext())
                        .setMessage(getString(R.string.location_permission_needed))
                        .setPositiveButton(getString(R.string.disable_geolocation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startSettingsActivity();
                            }
                        })
                        .create();

                messageDialog.show();
            }
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onLocationChanged(Location location) {
        geocodingService.refreshLocation(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void serviceSuccess(Channel channel) {

       // loadingDialog.dismiss();

        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        Condition[] forecast = channel.getItem().getForecast();

        int weatherIconImageResource = getResources().getIdentifier("icon_" + condition.getCode(), "drawable", getActivity().getPackageName());

        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(getString(R.string.temperature_output, condition.getTemperature(), units.getTemperature()));
        conditionTextView.setText(condition.getDescription());
        locationTextView.setText(channel.getLocation());

        for (int day = 0; day < forecast.length; day++) {
            if (day >= 5) {
                break;
            }

            Condition currentCondition = forecast[day];

            int viewId = getResources().getIdentifier("forecast_" + day, "id", getActivity().getPackageName());
            WeatherConditionFragment fragment = (WeatherConditionFragment) getChildFragmentManager().findFragmentById(viewId);

            if (fragment != null) {
                fragment.loadForecast(currentCondition, channel.getUnits());

            }
        }

        cacheService.save(channel);
    }

    @Override
    public void serviceFailure(Exception exception) {

        // display error if this is the second failure
        if (weatherServicesHasFailed) {
            //loadingDialog.dismiss();
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            // error doing reverse geocoding, load weather data from cache
            weatherServicesHasFailed = true;
            // OPTIONAL: let the user know an error has occurred then fallback to the cached data
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();

            cacheService.load(this);
        }
    }

    @Override
    public void geocodeSuccess(LocationResult location) {

        // completed geocoding successfully
        weatherService.refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pref_cached_location), location.getAddress());
        editor.apply();
    }

    @Override
    public void geocodeFailure(Exception exception) {

        // GeoCoding failed, try loading weather data from the cache
        cacheService.load(this);
    }

    //Custom adapter class

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private ArrayList<EventModel> list;

        //constructor to populate the list
        public MyAdapter(ArrayList<EventModel> data) {
            this.list=data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Setting up the ui

            View v= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_item_for_home_frag,parent,false);
            //calling the custom view holder class

            MyViewHolder holder=new MyViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            //setting the parameters for the views

          //  holder.eName.setText(list.get(position).getEventName()); //using the EventModel class to get the values of ArrayList
            holder.eDesti.setText(list.get(position).getEventDesti());
            holder.eDate.setText(list.get(position).getEventDate());
            holder.eTime.setText(list.get(position).getEventTime());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView eName,eDesti,eDate,eTime;

        public MyViewHolder(View v) {
            super(v);

            //initializing the views
           // eName=(TextView)v.findViewById(R.id.setENameHome);
            eDesti=(TextView)v.findViewById(R.id.setEDestinationHome);
            eDate=(TextView)v.findViewById(R.id.setEDateHome);
            eTime=(TextView)v.findViewById(R.id.setETimeHome);
        }
    }



    //initialize the list
    private void initializeList() {
        //getting data from the database and populating the list

        EventDatabaseOpenHelper mDbhelper=new EventDatabaseOpenHelper(getContext());
        SQLiteDatabase db=mDbhelper.getReadableDatabase();
        String query = "SELECT * FROM " + EventDatabase.DefineTable.TABLE_NAME;

        Cursor res = db.rawQuery(query,null);

        while (res.moveToNext()) {
            eventName=res.getString(0);
            eventDesti=res.getString(1);
            eventDate=res.getString(2);
            eventTime=res.getString(3);
            eventDes=res.getString(4);
            eventBudget=res.getString(5);
            delId=res.getString(6);
            eventList.add(new EventModel(eventName,eventDesti,eventDate,eventTime,eventDes,eventBudget,delId));
        }


    }

}
