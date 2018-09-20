package com.imaginers.onirban.tourit.listener;


import com.imaginers.onirban.tourit.data.LocationResult;

public interface GeocodingServiceListener {
    void geocodeSuccess(LocationResult location);

    void geocodeFailure(Exception exception);
}
