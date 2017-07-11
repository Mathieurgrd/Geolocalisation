package com.example.mathieu.geolocalisationquest;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by mathieu on 11/07/17.
 */

public class ForecastWeatherRequest extends GoogleHttpClientSpiceRequest<Forecast> {

    private String baseUrl;

    public ForecastWeatherRequest(double latitude, double longitude, String apiKey) {
        super(Forecast.class);
        this.baseUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&lang=fr";
    }

    @Override
    public Forecast loadDataFromNetwork() throws Exception {
        Ln.d("Call web service " + baseUrl);
        Forecast request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(this.baseUrl))
                .setParser(new GsonFactory().createJsonObjectParser())
                .execute()
                .parseAs(getResultType());
        return request;

    }
}
