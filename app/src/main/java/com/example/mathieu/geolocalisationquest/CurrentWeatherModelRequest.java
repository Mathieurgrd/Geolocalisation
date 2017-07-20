package com.example.mathieu.geolocalisationquest;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by mathieu on 11/07/17.
 */


public class CurrentWeatherModelRequest extends GoogleHttpClientSpiceRequest<WeatherModel> {

    private Double latitude;
    private Double longitude;
    private String apiKey;

    private String baseUrl;

    public CurrentWeatherModelRequest(double latitude, double longitude, String apiKey) {
        super(WeatherModel.class);
        this.baseUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&lang=fr";

    }

    @Override
    public WeatherModel loadDataFromNetwork() throws Exception {
        Ln.d("Call web service " + baseUrl);
        WeatherModel request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(this.baseUrl))
                .setParser(new GsonFactory().createJsonObjectParser())
                .execute()
                .parseAs(getResultType());
        return request;

    }
}