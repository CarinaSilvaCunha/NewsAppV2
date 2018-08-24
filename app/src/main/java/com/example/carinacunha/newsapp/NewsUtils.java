package com.example.carinacunha.newsapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.example.carinacunha.newsapp.News.News;

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

public class NewsUtils {

    private static final String LOG_TAG = NewsUtils.class.getSimpleName();

    // Keys used for the JSON response
    private static final String response = "response";
    private static final String results = "results";
    private static final String section = "sectionName";
    private static final String date = "webPublicationDate";
    private static final String title = "webTitle";
    private static final String url = "webUrl";
    private static final String tags = "tags";
    private static final String author = "webTitle";
    private static final String fields = "fields";
    private static final String thumbnail = "thumbnail";


    // Create static class
    private NewsUtils() {
    }

    // Query the Guardian API and return a list of News
    // transform the string URL into an URL object
    // Do an http request to the URL object and get a JSON response
    public static List<News> queryNews(String newsURL) {
        URL url = transformURL(newsURL);
        String queryResponse = null;
        try {
            queryResponse = httpRequest(url);
        } catch (IOException e) {
            String errorMessage = Resources.getSystem().getString(R.string.http_failed);
            Log.e(LOG_TAG, errorMessage, e);
        }
        List<News> news = parseJSON(queryResponse);

        // Return the list of news
        return news;
    }

    // Transform the String URL into an object URL
    private static URL transformURL(String newsURL) {
        URL url = null;
        try {
            url = new URL(newsURL);
        } catch (MalformedURLException e) {
            String errorMessage = Resources.getSystem().getString(R.string.url_transf_failed);
            Log.e(LOG_TAG, errorMessage, e);
        }
        return url;
    }

    // Make a http request to the url and return the json response
    // If the URL is null then return empty.
    // If the URL has a valid URL, then establish a connection for the InputStream
    // Timeouts are in milliseconds
    // If the connection succeeds, read the Input Stream and parse the response
    // Close the connection either way
    private static String httpRequest(URL apiURL) throws IOException {
        String queryResponse = "";
        HttpURLConnection connection = null;
        InputStream stream = null;

        if (apiURL == null) {
            return queryResponse;
        }
        try {
            connection = (HttpURLConnection) apiURL.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                queryResponse = readFromStream(stream);
            } else {
                String errorMessage = Resources.getSystem().getString(R.string.error_http);
                Log.e(LOG_TAG, errorMessage + connection.getResponseCode());
            }
        } catch (IOException e) {
            String errorMessage = Resources.getSystem().getString(R.string.ioException);
            Log.e(LOG_TAG, errorMessage, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return queryResponse;
    }

    // Here we'll convert the Input Stream into a JSON response String
    // Add the response of the BufferedReader line by line to the StringBuilder
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder responseOutput = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                responseOutput.append(line);
                line = reader.readLine();
            }
        }
        return responseOutput.toString();
    }

    // Here we'll create a list of News that has been created by the parsing of the JSON response
    // If the result is empty or null, stop
    // Create an empty News ArrayList so for each new news article will create a new news object
    // Extract the information needed using their key's
    // From here we create a new news article with all the fields
    private static List<News> parseJSON(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> news = new ArrayList<>();

        try {
            JSONObject fullJsonResponse = new JSONObject(newsJSON);
            JSONObject jsonNewsResponse = fullJsonResponse.getJSONObject(response);
            JSONArray newsArray = jsonNewsResponse.getJSONArray(results);

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);
                String newsSection = currentNews.getString(section);
                String newsTitle = currentNews.getString(title);
                String newsUrl = currentNews.getString(url);
                String newsDate;
                if (currentNews.has(date)) {
                    newsDate = currentNews.getString(date);
                } else {
                    newsDate = "Not Available";
                }
                JSONArray currentNewsAuthorArray = currentNews.getJSONArray(tags);
                String newsAuthor;
                int tagsLength = currentNewsAuthorArray.length();
                if (tagsLength == 1) {
                    JSONObject currentNewsAuthor = currentNewsAuthorArray.getJSONObject(0);
                    String newsAuthor1 = currentNewsAuthor.getString(author);
                    newsAuthor = "Author: " + newsAuthor1;
                } else {
                    newsAuthor = "Not Available";
                }

                JSONObject currentFields = currentNews.getJSONObject(fields);
                Drawable newsImage = getNewsImage(currentFields.getString(thumbnail));

                News newNews = new News(newsTitle, newsSection, newsAuthor, newsDate, newsUrl, newsImage);
                news.add(newNews);
            }

        } catch (JSONException e) {
            String errorMessage = Resources.getSystem().getString(R.string.jsonException);
            Log.e("NewsUtils", errorMessage);
        }

        return news;
    }

    private static Drawable getNewsImage(String newsURL) {
        URL url = transformURL(newsURL);
        if (url == null) {
            return null;
        }
        Drawable img = null;
        try {
            InputStream inputStream = (InputStream) url.getContent();
            img = Drawable.createFromStream(inputStream, newsURL);
        } catch (IOException e) {
            String errorMessage = Resources.getSystem().getString(R.string.exception_image);
            Log.e(LOG_TAG, errorMessage, e);
        }
        return img;
    }
}

