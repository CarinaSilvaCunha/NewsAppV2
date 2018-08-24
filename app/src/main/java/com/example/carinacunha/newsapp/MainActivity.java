package com.example.carinacunha.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carinacunha.newsapp.News.NewsAdapter;
import com.example.carinacunha.newsapp.News.NewsLoader;
import com.example.carinacunha.newsapp.News.News;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    // Here we find the necessary information to get the information from the API
    private static final String API_URL = "http://content.guardianapis.com/search?";

    // Constant value for the API Key
    private static final String KEY = "97daca29-4108-49f1-92ff-fd50091aee00";

    // Empty text view
    private TextView default_view;

    // NewsAdapter
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_news);

        // In the layout list_news.xml , find the ListView
        ListView list_view = findViewById(R.id.list_view);

        // When no news are found, show the default view on the screen
        default_view = findViewById(R.id.default_view);
        list_view.setEmptyView(default_view);

        // Create a new adapter and connect it to its part on the list_news.xml
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        list_view.setAdapter(newsAdapter);

        // This onItemClick listener will look for the item that was clicked, get the URL, convert it
        // and create an intent with it. Will display a toast if there's no application on the device to perform the intent
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                News selectedNews = newsAdapter.getItem(position);
                assert selectedNews != null;
                Uri selectedNewsURI = Uri.parse(selectedNews.getNewsUrl());
                Intent openSelectedNews = new Intent(Intent.ACTION_VIEW, selectedNewsURI);

                PackageManager packageManagerOpenNews = getPackageManager();
                List<ResolveInfo> resolveInfo = packageManagerOpenNews.queryIntentActivities(openSelectedNews, PackageManager.MATCH_DEFAULT_ONLY);
                boolean hasApplicationForWebBrowsing = resolveInfo.size() > 0;
                if (hasApplicationForWebBrowsing) {
                    startActivity(openSelectedNews);
                } else {
                    String no_browser_found = getString(R.string.no_browser_found);
                    Toast.makeText(MainActivity.this, no_browser_found, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Check the state of network connectivity
        // If there's a connection, fetch data through the LoadManager
        // If there's no connection, show message requesting connection and refresh
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cManager != null;
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        if (nInfo != null) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(R.integer.loader, null, this);
        } else {
            View progress_bar = findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.GONE);
            String connection_unavailable = getString(R.string.connection_unavailable);
            warningMessage(connection_unavailable);
        }

        // Create a the swiperefreshlayout and connect it to its part on the list_news.xml
        // when you refresh/swipe down, it will restart the newsLoader.
        final SwipeRefreshLayout swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                restartNewsLoader();
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }


    // Create a new loader for the given API ordered by the most recent
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder queryBuilder = baseUri.buildUpon();
        queryBuilder.appendQueryParameter(getString(R.string.order_by), getString(R.string.newest));
        queryBuilder.appendQueryParameter(getString(R.string.show_fields), getString(R.string.thumbnail));
        queryBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.contributor));
        queryBuilder.appendQueryParameter(getString(R.string.page_size), getString(R.string.page_size_number));
        queryBuilder.appendQueryParameter(getString(R.string.api_key), KEY);
        return new NewsLoader(this, queryBuilder.toString());
    }

    // After loading is done, hide the progressbar
    // If there's news, add them to the adapter, if not, set the state as empty
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        View progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        newsAdapter.clear();
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
            if (news.isEmpty()) {
                String unexpected_behaviour = getString(R.string.unexpected_behaviour);
                warningMessage(unexpected_behaviour);
            }
        }
    }

    // On Loader reset, we clear existing news
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    // Warn the user about an unexpected situation
    private void warningMessage(String warningMessage) {
        View progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);
        default_view.setVisibility(View.VISIBLE);
        default_view.setText(warningMessage);
    }

    // On resume, restart loader
    @Override
    protected void onResume() {
        super.onResume();
        restartLoader();
    }

    // Restart news loader
    public void restartNewsLoader() {
        restartLoader();
    }

    // Restart loader
    private void restartLoader() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(R.integer.loader, null, this);
    }

}

