package com.example.android.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private static final String DATE_SPLITTER = "T";
    private static final String TIME_SPLITTER = "Z";
    private Context mContext;
    private List<News> mNewsList;

    public NewsAdapter(Context context, List<News> NewsList) {
        this.mContext = context;
        this.mNewsList = NewsList;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public News getItem(int position) {
        return mNewsList.get(position);
    }

    public void addNews(List<News> data) {
        mNewsList.addAll(data);
    }

    public void getClean() {
        mNewsList.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final News currentNews = mNewsList.get(position);

        // Get the object located at this position in the list
        holder.title.setText(currentNews.getTitle());
        holder.name.setText(currentNews.getName());
        holder.author.setText(currentNews.getAuthor());

        // Display the author of the current News
        if (currentNews.getAuthor() != null) {
            holder.author.setText(currentNews.getAuthor());
        } else {
            holder.author.setVisibility(View.INVISIBLE);
        }

        String DateTime = currentNews.getDate();
        String News_Date = "";
        String News_Time = "";

        //Separate DateTime field to different variables
        if (DateTime.contains(DATE_SPLITTER)) {
            String[] date_time = DateTime.split(DATE_SPLITTER);
            News_Date = date_time[0];

            if (date_time[1].contains(TIME_SPLITTER)) {
                News_Time = date_time[1].replace(TIME_SPLITTER, "");
            } else {
                News_Time = date_time[1];
            }
        }

        if (News_Date != null) {
            // Format the date string (i.e. "Mar 3, 1984")
            String formattedDate = null;
            formattedDate = formatDate(News_Date);
            holder.date.setText(formattedDate);
        } else {
            holder.date.setVisibility(View.GONE);
        }

        if (News_Time != null) {
            // Format the time string (i.e. "4:30PM")
            String formattedTime = null;
            formattedTime = formatTime(News_Time);
            holder.time.setText(formattedTime);
        } else {
            holder.time.setVisibility(View.GONE);
        }

        //Launch the webPage when the News is clicked.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri NewsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, NewsUri);
                mContext.startActivity(websiteIntent);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView name;
        private TextView author;
        private TextView date;
        private TextView time;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.news_title);
            name = (TextView) view.findViewById(R.id.section_name);
            author = (TextView) view.findViewById(R.id.news_author);
            date = (TextView) view.findViewById(R.id.news_date);
            time = (TextView) view.findViewById(R.id.news_time);
        }
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String date) {
        String newDate = null;
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("LLL dd, yyyy");

        try {
            newDate = newFormat.format(oldFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    /**
     * Return the formatted time string (i.e. "4:30 PM")
     */
    private String formatTime(String time) {
        String newTime = null;
        SimpleDateFormat oldFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("hh:mm a");

        try {
            newTime = newFormat.format(oldFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }
}
