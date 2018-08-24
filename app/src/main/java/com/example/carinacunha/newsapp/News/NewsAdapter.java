package com.example.carinacunha.newsapp.News;

import com.example.carinacunha.newsapp.R;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    // Verify if the view is being used and inflate view
    // Get the current news object and insert the information retrieved in the views on activity_main.xml layout
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_main, parent, false);
        }

        News currentNews = getItem(position);
        TextView news_title = listItemView.findViewById(R.id.news_title);
        assert currentNews != null;
        news_title.setText(currentNews.getNewsTitle());

        ImageView news_image = listItemView.findViewById(R.id.news_image);
        news_image.setImageDrawable(currentNews.getNewsImage());

        TextView news_section = listItemView.findViewById(R.id.news_section);
        news_section.setText(currentNews.getNewsSection());

        TextView news_author = listItemView.findViewById(R.id.news_author);
        news_author.setText(currentNews.getNewsAuthor());

        TextView news_date = listItemView.findViewById(R.id.news_date);
        String newsDate = formatDate(currentNews.getNewsDate());
        news_date.setText(newsDate);

        return listItemView;
    }

    // Format the date into a more readable date
    private static String formatDate(String input) {
        SimpleDateFormat formatInput =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat formatOutput =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String output = null;
        try {
            Date dt = formatInput.parse(input);
            output = formatOutput.format(dt);
        } catch (ParseException e) {
            String errorMessage = Resources.getSystem().getString(R.string.date_error);
            Log.e(LOG_TAG, errorMessage, e);
        }
        return output;
    }

}

