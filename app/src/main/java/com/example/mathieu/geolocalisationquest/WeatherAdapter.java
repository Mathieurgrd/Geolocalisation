package com.example.mathieu.geolocalisationquest;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by apprenti on 11/05/17.
 */

public class WeatherAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<List> mForecastModelList;

    public WeatherAdapter (Context context, ArrayList<List> forecastModelList){
        mContext = context;
        mForecastModelList = forecastModelList;
    }

    @Override
    public int getCount() {
        return mForecastModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mForecastModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.weather_item, parent, false);
        }

        List currentModel = (List)getItem(position);
        double cTemp = currentModel.getMain().getTemp()-273.15;
        String weather = currentModel.getWeather().get(0).getDescription();


        TextView textViewDate = (TextView)convertView.findViewById(R.id.textViewDate);
        TextView textViewTemp = (TextView)convertView.findViewById(R.id.textViewTemp);
        TextView textViewWeather = (TextView)convertView.findViewById(R.id.textViewWeather);
        ImageView gifTextViewWeather = (ImageView) convertView.findViewById(R.id.gifTextViewWeather);

        textViewDate.setText(getDate(currentModel.getDt(),"dd/MM/yyyy HH:mm "));
        textViewTemp.setText(String.valueOf((int)(cTemp)+ " Degrès"));
        textViewWeather.setText(currentModel.getWeather().get(0).getDescription());

        switch (weather){
            case ("ciel dégagé"):
                gifTextViewWeather.setBackgroundResource(R.drawable.index);
                //   convertView.setBackgroundResource(R.drawable.index);
                break;
            case ("partiellement ensoleillé"):
                gifTextViewWeather.setBackgroundResource(R.drawable.partielenso);
                //   convertView.setBackgroundResource(R.drawable.partielenso);
                break;
            case ("légères pluies"):
                gifTextViewWeather.setBackgroundResource(R.drawable.pluie);
                //  convertView.setBackgroundResource(R.drawable.pluie);
                break;
            case ("pluies modérées"):
                gifTextViewWeather.setBackgroundResource(R.drawable.lightrain);
               // convertView.setBackgroundResource(R.drawable.lightrain);
                break;
            case ("nuageux"):
                gifTextViewWeather.setBackgroundResource(R.drawable.cloudy);
                //  convertView.setBackgroundResource(R.drawable.cloudy);
                break;
            case ("peu nuageux"):
                gifTextViewWeather.setBackgroundResource(R.drawable.cloudsun);
                //  convertView.setBackgroundResource(R.drawable.cloudsun);
                break;
            case ("couvert"):
                gifTextViewWeather.setBackgroundResource(R.drawable.couvert);
                //  convertView.setBackgroundResource(R.drawable.couvert);
                break;
            default:
                gifTextViewWeather.setBackgroundResource(R.drawable.index);



        }

        return convertView;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds*1000);
        return formatter.format(calendar.getTime());
    }
}