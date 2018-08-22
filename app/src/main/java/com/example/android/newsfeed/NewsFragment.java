package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays the News.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int NEWS_LOADER_ID = 1;
    private TextView mEmptyView;
    private ProgressBar progressBar;
    private boolean isNetworkAvailable;
    private RecyclerView mRecyclerView;
    private Context context;
    private String ApiKey = BuildConfig.ApiKey;
    private String REQUEST_URL = null;
    private Uri.Builder builtUri;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    /** Adapter for the list of News */
    private NewsAdapter mAdapter;

    /** URL for the Guardian*/
    private static final String URL= "https://content.guardianapis.com/search";

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        //Listener on changed preference:
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                getActivity().getLoaderManager().restartLoader(NEWS_LOADER_ID, null, NewsFragment.this);
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);

        //Retrieves the category key from sharedPrefs instance
        String category= sharedPrefs.getString(
                getString(R.string.settings_category_key),
                getString(R.string.settings_category_default));

        //Order by preferences
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        //Limit of News to display
        String news_limit  = sharedPrefs.getString(
                getString(R.string.news_limit_key),
                getString(R.string.news_limit_default)
        );

        //Setting custom from date
        String fromDate  = sharedPrefs.getString(
                getString(R.string.from_date_key),
                getString(R.string.from_date_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        builtUri = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        builtUri.appendQueryParameter("format", "json");
        builtUri.appendQueryParameter("from-date", fromDate);
        builtUri.appendQueryParameter("show-fields", "byline");
        if (!"all".equals(category)){builtUri.appendQueryParameter("section", category);}
        builtUri.appendQueryParameter("order-by", orderBy);
        builtUri.appendQueryParameter("page-size", news_limit);
        builtUri.appendQueryParameter("api-key", ApiKey);

        REQUEST_URL = builtUri.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_activity, container, false);
        context = rootView.getContext();

        // Initialize loader
        getActivity().getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);

        //Checking if there is internet available and getting connected too.
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isNetworkAvailable = (networkInfo != null && networkInfo.isConnected());

        //Initializing the empty and recyclerView.
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Set state to progress bar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        // Set the adapter on the {@link RecyclerView}
        mAdapter = new NewsAdapter(context, new ArrayList<News>());
        mRecyclerView.setAdapter(mAdapter);

        if (isNetworkAvailable){
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else{
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    public NewsAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(context, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        //Change the text style of the EmptyView
        mEmptyView.setTextColor(getResources().getColor(R.color.secondary_text));
        mEmptyView.setAllCaps(true);
            
        if (!isNetworkAvailable){
            //Display no connection message
            mEmptyView.setText(R.string.internet);
        } else{
            // Set empty state text to display "No News found."
            mEmptyView.setText(R.string.empty);
        }

        //Set state to progress bar
        progressBar.setVisibility(View.GONE);

        //Clear previous data
        mAdapter.getClean();

        // If there is a valid list of {@link News}, then add them to the adapter's data set.
        if (data != null && !data.isEmpty()) {
            mAdapter.addNews(data);
        } else{
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.getClean();
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Set up a listener whenever a key changes
        getActivity().getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }
}

