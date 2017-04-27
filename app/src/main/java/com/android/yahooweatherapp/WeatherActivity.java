package com.android.yahooweatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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
    private WebView mQueryWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mWeatherTextView = (TextView) findViewById(R.id.response_text_view);

        mQueryText = (EditText) findViewById(R.id.query_text_input);

        //mQueryText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            //@Override
            //public void onFocusChange(View v, boolean hasFocus) {
                //Toast.makeText(getBaseContext(),
                        //(v).getId() + " has focus - " + hasFocus,
                        //Toast.LENGTH_LONG).show();
                //if(!hasFocus)
                    //Toast.makeText(getApplicationContext(), "Location", Toast.LENGTH_LONG).show();
            //}
        //});

        mQueryButton = (Button) findViewById(R.id.query_button);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String input = mQueryText.getText().toString().trim();
                if (input.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a location (e.g. 97140)", Toast.LENGTH_LONG).show();
                } else {
                    getWeatherInfoBetter(input);
                }
            }
        });

        mQueryWebView = (WebView) findViewById(R.id.query_web_view);
    }

    private void getWeatherInfoBetter(String input) {
        String url = getWeatherUrl(input);
        if (url == null) {
            Toast.makeText(getApplicationContext(), "Entered location could not be used, please try again.", Toast.LENGTH_LONG).show();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String temperature;
                        Log.d(TAG, "Weather API response: \n" + response.toString());

                        try {
                            JSONObject channel = response.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
                            temperature = channel.getJSONObject("item").getJSONObject("condition").getString("temp");
                        } catch (JSONException e) {
                            temperature = "-100";
                            e.printStackTrace();
                        }

                        mWeatherTextView.setText("Temperature: " + temperature);
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

    private void getWeatherInfo(String input) {
        String url;
        final String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
        RequestQueue queue = Volley.newRequestQueue(this);

        String encodedCriteria = "";
        String tempCriteria = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text='" + input +"')";
        try {
            encodedCriteria = URLEncoder.encode(tempCriteria, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        url = baseUrl + encodedCriteria + "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = "";
                        // Display the first 1000 characters of the response string.
                        try {
                            JSONObject reader = new JSONObject(response);
                            JSONObject query = reader.getJSONObject("query").getJSONObject("results")
                                    .getJSONObject("channel").getJSONObject("item").getJSONObject("condition");
                            result = query.getString("temp");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mWeatherTextView.setText("Response is: "+ result.substring(0,1000));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mWeatherTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
