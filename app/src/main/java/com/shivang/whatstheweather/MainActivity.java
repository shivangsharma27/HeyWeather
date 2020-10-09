package com.shivang.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    public class DownloadTask extends AsyncTask<String,Void,String> {
        String result = "";
        URL url;
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... urls) {
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                String tempInfo = jsonObject.getString("main");
                String visibility = jsonObject.getString("visibility");
//                tempInfo = "[" + tempInfo + "]";
//                JSONArray jsonArray = new JSONArray(tempInfo);
//                JSONObject jsonPart2 = jsonArray.getJSONObject(0);
//                String temp = jsonPart2.getString("temp");
//                String humidity = jsonPart2.getString("humidity");


                JSONObject jsonObject1 = new JSONObject(tempInfo);
                String temp = jsonObject1.getString("temp");
                String humidity = jsonObject1.getString("humidity");
                String pressure = jsonObject1.getString("pressure");
                String feelsLike = jsonObject1.getString("feels_like");

                JSONArray array = new JSONArray(weatherInfo);
                String message = "";
                for(int i = 0; i<array.length(); i++){
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += main + ": " + description + "\r\n";
                    }
                }
                if(!temp.equals(""))
                    message += "Temp: " + temp + "°C" + "\r\n";
                if(!feelsLike.equals(""))
                    message += "Feels Like: " + feelsLike + "°C" +"\r\n";
                if(!humidity.equals(""))
                    message += "Humidity: " + humidity + "%" + "\r\n";
                if(!pressure.equals(""))
                    message += "Pressure: " + pressure + " mb" + "\r\n";
                if(!visibility.equals(""))
                    message += "Visibility: " + visibility + " m";

                if(!message.equals("")){
                    textView.setText(message);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find Weather", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView2);
    }
}