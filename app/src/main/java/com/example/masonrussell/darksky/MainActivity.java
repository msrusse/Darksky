package com.example.masonrussell.darksky;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    public EditText enterCity;
    public LinearLayout weatherInfoLayout;
    public TextView summaryResult, tempResult, dewPointResult, precipResult, uvResult, windResult, humidityResult, feelsLikeResult;
    public Button getWeather;
    public String summary, latitude, longitude;
    public Double temp, dewPoint, humidity, precipProb, uvIndex, windSpeed, feelsLike;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterCity = findViewById(R.id.enterCity);
        weatherInfoLayout = findViewById(R.id.weatherInfoLayout);
        getWeather = findViewById(R.id.button);
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        summaryResult = findViewById(R.id.summaryResult);
        tempResult = findViewById(R.id.tempResult);
        getWeather = findViewById(R.id.button);
        dewPointResult = findViewById(R.id.dewPointResult);
        precipResult = findViewById(R.id.precipResult);
        uvResult = findViewById(R.id.uvResult);
        windResult = findViewById(R.id.windResult);
        humidityResult = findViewById(R.id.humidityResult);
        feelsLikeResult = findViewById(R.id.feelsLikeResult);
        enterCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterCity.setText("");
            }
        });
        getWeather.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String city = enterCity.getText().toString();
                StringBuilder cityEdited = new StringBuilder(city);

                for (int i =0; i < city.length(); i++)
                {
                    if (city.charAt(i) == ' ')
                    {
                        cityEdited.setCharAt(i, '+');
                    }
                }
                MainActivity.GoogleLongLatHttpRequest googleLongLatHttpRequest = new MainActivity.GoogleLongLatHttpRequest();
                googleLongLatHttpRequest.execute(cityEdited.toString());
            }
        });
        //MainActivity.DarkskyHttpGetRequest darkskyHttpGetRequest = new MainActivity.DarkskyHttpGetRequest();
        //darkskyHttpGetRequest.execute(urlDarkSky);
    }

    public class DarkskyHttpGetRequest extends AsyncTask<String, Void, String>
    {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            try
            {
                JSONObject jo = new JSONObject(result).getJSONObject("currently");

                temp = jo.getDouble("temperature");
                summary = jo.getString("summary");
                dewPoint = jo.getDouble("dewPoint");
                humidity = jo.getDouble("humidity");
                precipProb = jo.getDouble("precipProbability");
                uvIndex = jo.getDouble("uvIndex");
                windSpeed = jo.getDouble("windSpeed");
                feelsLike = jo.getDouble("apparentTemperature");
            }
            catch (JSONException e)
            {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
            SetText();
        }
    }

    public class GoogleLongLatHttpRequest extends AsyncTask<String, Void, String>
    {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params){
            String urlCity = params[0];
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + urlCity + "&key=AIzaSyCqyiOYmkzaqizu-XKBoL6HL2t3Cuqr9rA";
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();

            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            try
            {
                JSONArray ja = new JSONObject(result).getJSONArray("results");
                JSONObject jo = ja.getJSONObject(0);
                JSONObject geo = jo.getJSONObject("geometry");
                //for (int i=0; i < jo.length(); i++)
                /*{
                    if (jo.getString("geometry").equals("geometry"))
                    {

                    }
                }*/
                JSONObject location = geo.getJSONObject("location");
                latitude = location.getString("lat");
                longitude = location.getString("lng");
                //JSONObject location = geo.getJSONObject("location");
                //latitude = location.getString("lat");
                //longitude = location.getString("lng");
                String urlDarkSky = "https://api.darksky.net/forecast/240d8f1caed0393567813137b7215015/" + latitude + "," + longitude + "?exclude=minutely,hourly,daily,alerts,flags";
                MainActivity.DarkskyHttpGetRequest darkskyHttpGetRequest = new MainActivity.DarkskyHttpGetRequest();
                darkskyHttpGetRequest.execute(urlDarkSky);

            }
            catch (JSONException e)
            {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
            //String urlDarkSky = "https://api.darksky.net/forecast/240d8f1caed0393567813137b7215015/" + latitude + "," + longitude + "?exclude=minutely,hourly,daily,alerts,flags";
            //MainActivity.DarkskyHttpGetRequest darkskyHttpGetRequest = new MainActivity.DarkskyHttpGetRequest();
            //darkskyHttpGetRequest.execute(urlDarkSky);
        }
    }

    public void SetText()
    {
        summaryResult.setText(summary);
        String tempDisplay = String.valueOf(temp) + "˚F";
        tempResult.setText(tempDisplay);
        String feelsLikeDisplay = String.valueOf(feelsLike) + "˚F";
        feelsLikeResult.setText(feelsLikeDisplay);
        String dewDisplay = String.valueOf(dewPoint) + "˚F";
        dewPointResult.setText(dewDisplay);
        Double humidityPerc = humidity*100;
        String humidityDisplay = String.valueOf(humidityPerc) + "%";
        humidityResult.setText(humidityDisplay);
        Double precipPerc = precipProb*100;
        String precipDisplay = String.valueOf(precipPerc) + "%";
        precipResult.setText(precipDisplay);
        uvResult.setText(String.valueOf(uvIndex));
        String windDisplay = String.valueOf(windSpeed) + " mph";
        windResult.setText(windDisplay);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }
}
