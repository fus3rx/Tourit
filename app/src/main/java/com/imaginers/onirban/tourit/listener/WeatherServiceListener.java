package com.imaginers.onirban.tourit.listener;


import com.imaginers.onirban.tourit.data.Channel;

public interface WeatherServiceListener {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}
