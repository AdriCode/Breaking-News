package com.example.android.newsfeed;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    public static final int READ_TIME_OUT = 10000;
    public static final int CONNECT_TIME_OUT = 15000;

    private static List<News> extractFeatures(String NewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }

        List<News> news_list = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(NewsJSON);

            // Extract the JSONObject associated with the key called "response",
            JSONObject response = baseJsonResponse.getJSONObject("response");

            //Extract the JSONArray associated with the key called "results"
            JSONArray NewsArray = response.getJSONArray("results");

            for (int i = 0; i < NewsArray.length(); i++) {

                // Get a single result at position i within the list of News
                JSONObject fields = NewsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = fields.optString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = fields.optString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String date = fields.optString("webPublicationDate");

                // Extract the value for the key called "webUrl"
                String url = fields.optString("webUrl");

                // Extract the value author for the key called "fields"
                String author = null;

                if (fields.has("fields")){
                    JSONObject extraInfo = fields.getJSONObject("fields");
                    author = extraInfo.optString("byline");
                }

                // Create a new {@link News} object
                News news = new News(title, sectionName, author, date, url);

                // Add the new {@link news} to the list of News.
                news_list.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of News
        return news_list;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> News_list = extractFeatures(jsonResponse);

        // Return the list of {@link News}
        return News_list;
    }
}
