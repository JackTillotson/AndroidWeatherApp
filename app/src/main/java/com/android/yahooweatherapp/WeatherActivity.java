package com.android.yahooweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class WeatherActivity extends AppCompatActivity {

    public static final String TAG = "WeatherApp";

    private Button mQueryButton;
    private TextView mWeatherTextView;
    private EditText mQueryText;
    private Button mMapsButton;
    private TableLayout mTableLayout1;
    private TextView mRow1Line1;
    private TextView mRow1Line2;
    private TextView mRow2Line1;
    private TextView mRow2Line2;
    private TextView mRow3Line1;
    private TextView mRow3Line2;
    private TextView mRow4Line1;
    private TextView mRow4Line2;
    private TextView mRow5Line1;
    private TextView mRow5Line2;
    private TextView mRow6Line1;
    private TextView mRow6Line2;
    private TextView mRow7Line1;
    private TextView mRow7Line2;
    private TextView mRow8Line1;
    private TextView mRow8Line2;

    String mLatitude;
    String mLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mWeatherTextView = (TextView) findViewById(R.id.response_text_view);
        mRow1Line1 = (TextView) findViewById(R.id.R1L1);
        mRow1Line2 = (TextView) findViewById(R.id.R1L2);
        mRow2Line1 = (TextView) findViewById(R.id.R2L1);
        mRow2Line2 = (TextView) findViewById(R.id.R2L2);
        mRow3Line1 = (TextView) findViewById(R.id.R3L1);
        mRow3Line2 = (TextView) findViewById(R.id.R3L2);
        mRow4Line1 = (TextView) findViewById(R.id.R4L1);
        mRow4Line2 = (TextView) findViewById(R.id.R4L2);
        mRow5Line1 = (TextView) findViewById(R.id.R5L1);
        mRow5Line2 = (TextView) findViewById(R.id.R5L2);
        mRow6Line1 = (TextView) findViewById(R.id.R6L1);
        mRow6Line2 = (TextView) findViewById(R.id.R6L2);
        mRow7Line1 = (TextView) findViewById(R.id.R7L1);
        mRow7Line2 = (TextView) findViewById(R.id.R7L2);
        mRow8Line1 = (TextView) findViewById(R.id.R8L1);
        mRow8Line2 = (TextView) findViewById(R.id.R8L2);


        mQueryText = (EditText) findViewById(R.id.query_text_input);
        mTableLayout1 = (TableLayout) findViewById(R.id.table_layout_1);

        mQueryButton = (Button) findViewById(R.id.query_button);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mMapsButton.setVisibility(View.VISIBLE);
                String input = mQueryText.getText().toString().trim();
                if (input.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a location (e.g. 97140)", Toast.LENGTH_LONG).show();
                } else {
                    getWeatherInfo(input);
                }
            }
        });


        mMapsButton = (Button) findViewById(R.id.maps_button);
        mMapsButton.setVisibility(View.INVISIBLE);

    }

    private void getWeatherInfo(String input) {
        String url = getWeatherUrl(input);
        if (url == null) {
            Toast.makeText(getApplicationContext(), "Entered location could not be used, please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String temperature = null;
                        String conditions = null;
                        String location;
                        String windSpeed = null;
                        String sunrise = null;
                        String sunset = null;
                        Log.d(TAG, "Weather API response: \n" + response.toString());

                        try {
                            JSONObject channel = response.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
                            temperature = channel.getJSONObject("item").getJSONObject("condition").getString("temp");
                            conditions = channel.getJSONObject("item").getJSONObject("condition").getString("text");
                            location = channel.getString("description");
                            windSpeed = channel.getJSONObject("wind").getString("speed");
                            sunrise = channel.getJSONObject("astronomy").getString("sunrise");
                            sunset = channel.getJSONObject("astronomy").getString("sunset");
                            mLatitude = channel.getJSONObject("item").getString("lat");
                            mLongitude = channel.getJSONObject("item").getString("long");
                        } catch (JSONException e) {
                            location = "That is not a real place.";
                            e.printStackTrace();
                        }

                        mMapsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View v) {
                                Intent i = new Intent();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com?q=" + mLatitude + "," + mLongitude));
                                startActivity(browserIntent);
                            }
                        });



                        mRow1Line1.setText(location);
                        mRow2Line1.setText("Temperature:"); mRow2Line2.setText(temperature);
                        mRow3Line1.setText("Current Conditions:"); mRow3Line2.setText(conditions);
                        mRow4Line1.setText("Wind Speed:"); mRow4Line2.setText(windSpeed);
                        mRow5Line1.setText("Sunrise:"); mRow5Line2.setText(sunrise);
                        mRow6Line1.setText("Sunset:"); mRow6Line2.setText(sunset);
                        mRow7Line1.setText("Latitude:"); mRow7Line2.setText(mLatitude);
                        mRow8Line1.setText("Longitude:"); mRow8Line2.setText(mLongitude);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mWeatherTextView.setText("That didn't work: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private String getWeatherUrl(String input) {
        final String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";

        String encodedCriteria;
        String tempCriteria = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text='" + input +"')";
        try {
            encodedCriteria = URLEncoder.encode(tempCriteria, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return baseUrl + encodedCriteria + "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    }

}
