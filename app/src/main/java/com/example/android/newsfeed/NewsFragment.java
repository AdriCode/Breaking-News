package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays "Science Category".
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int NEWS_LOADER_ID = 1;
    private TextView mEmptyView;
    private ProgressBar progressBar;
    private boolean isNetworkAvailable;
    private RecyclerView mRecyclerView;
    private Context context;
    String ApiKey = BuildConfig.ApiKey;
    private String REQUEST_URL = null;
    private Uri.Builder builtUri;

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

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        builtUri = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        builtUri.appendQueryParameter("format", "json");
        builtUri.appendQueryParameter("from-date", "2018-01-01");
        builtUri.appendQueryParameter("show-fields", "byline");
        builtUri.appendQueryParameter("order-by", "newest");
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
}
