package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.films.SingleFilmActivity;
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Film;

/**
 * Created by Bitizen on 02.11.16.
 */

public class FilmsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Film> filmList;
    private int futureColor;

    private long timestamp;

    public FilmsAdapter(Context context, List<Film> filmList, int futureColor) {
        this.context = context;
        this.filmList = filmList;
        this.futureColor = futureColor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilmsAdapter.FilmViewHolder(inflater.inflate(R.layout.item_film, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Film film = filmList.get(position);
        ((FilmViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleFilmActivity.class);
                intent.putExtra("id", film.getId());
                intent.putExtra("date", timestamp);
                context.startActivity(intent);
            }
        });

        Log.e("poster", film.getPoster());
        Picasso
                .with(context)
                .load(ServerApi.getImgUrl(film.getPoster(), true))
                .placeholder(R.drawable.ic_small_placeholder)
                .fit()
                .centerCrop()
                .into(((FilmViewHolder) holder).poster);

        ((FilmViewHolder) holder).rating.setText(String.valueOf(film.getRating()));
        if (film.getRating() != null && film.getRating().length() != 0) {
            if (Double.valueOf(film.getRating()) >= 7f)
                ((FilmViewHolder) holder).rating.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHighRating));
            else if (Double.valueOf(film.getRating()) >= 5f)
                ((FilmViewHolder) holder).rating.setBackgroundColor(ContextCompat.getColor(context, R.color.colorMiddleRating));
            else
                ((FilmViewHolder) holder).rating.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLowRating));
        } else
            ((FilmViewHolder) holder).rating.setVisibility(View.GONE);

        ((FilmViewHolder) holder).title.setText(film.getName() + ", " + film.getAge() + "+");

        if (film.getGenre() != null && film.getGenre().size() != 0) {
            String genre = film.getGenre().get(0);
            for (int i = 1; i < film.getGenre().size(); i++)
                genre += ", " + film.getGenre().get(i);
            ((FilmViewHolder) holder).genre.setText(genre);
        }
        if (film.getCountry() != null && film.getCountry().size() != 0) {
            String country = film.getCountry().get(0);
            for (int i = 1; i < film.getCountry().size(); i++)
                country += ", " + film.getCountry().get(i);
            ((FilmViewHolder) holder).country.setText(country);
        }
        if (film.getSeanse() != null && film.getSeanse().size() != 0) {
            String seanse = film.getSeanse().get(0);
            for (int i = 1; i < film.getSeanse().size(); i++)
                seanse += "    " + film.getSeanse().get(i);
            ((FilmViewHolder) holder).seanseTable.setText(seanse);
        }

//        String seanses = "";
//        boolean span = false;
//        int spanFuture = 0;
//        Date date = new Date();
//        for (int i = 0; i < film.getSeanse().size(); i++) {
//            String seanse = film.getSeanse().get(i);
//            Calendar calendar = Calendar.getInstance();
//            if (new Date(seanse).after(date) && !span) {
//                spanFuture = seanses.length() - 1;
//                span = true;
//            }
//            calendar.setTimeInMillis(seanse);
//            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
//            if (hour.length() == 1)
//                hour = "0" + hour;
//            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
//            if (minute.length() == 1)
//                minute = "0" + minute;
//            seanses += hour + ":" + minute + "   ";
//        }
//        SpannableString string = new SpannableString(seanses);
//        if (span)
//            string.setSpan(new ForegroundColorSpan(futureColor), spanFuture, seanses.length(), 0);
//        ((FilmViewHolder) holder).seanseTable.setText(string);
//
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private class FilmViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView poster;
        TextView rating;
        TextView title;
        TextView genre;
        TextView age;
        TextView country;
        TextView seanseTable;


        public FilmViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            rating = (TextView) itemView.findViewById(R.id.rating);
            title = (TextView) itemView.findViewById(R.id.title);
            genre = (TextView) itemView.findViewById(R.id.genre);
            age = (TextView) itemView.findViewById(R.id.age);
            country = (TextView) itemView.findViewById(R.id.country);
            seanseTable = (TextView) itemView.findViewById(R.id.seanse_table);
        }
    }
}
