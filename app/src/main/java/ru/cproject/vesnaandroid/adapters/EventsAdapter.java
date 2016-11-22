package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.events.SingleEventActivity;
import ru.cproject.vesnaandroid.obj.Event;

/**
 * Created by Bitizen on 03.11.16.
 */

public class EventsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Event> eventList;

    public EventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new EventViewHolder(lf.inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Event event = eventList.get(position);

        ((EventViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleEventActivity.class);
                intent.putExtra("id", event.getId());
                context.startActivity(intent);
            }
        });

        float dpi = context.getResources().getDisplayMetrics().density;

        Picasso
                .with(context)
                .load(event.getImage())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation((int)(4*dpi), 0, RoundedCornersTransformation.CornerType.TOP))
                .into(((EventViewHolder) holder).image);

        ((EventViewHolder) holder).title.setText(event.getTitle());
        ((EventViewHolder) holder).timestamp.setText(event.getTimestamp());
        ((EventViewHolder) holder).description.setText(event.getDescription());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private class EventViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView image;
        TextView title;
        TextView timestamp;
        TextView description;

        public EventViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
