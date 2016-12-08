package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.events.SingleEventActivity;
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Event;

/**
 * Created by Bitizen on 03.11.16.
 */

public class EventsAdapter extends RecyclerView.Adapter {

    protected final int EVENTS_ITEMS = 0;
    protected final int LOADING_ITEM = 1;
    protected final int ERROR_ITEM = 2;

    private int state = 1;
    public static final int DEFAULT = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;

    private Context context;
    private List<Event> eventList;
    protected int color;

    private RetryInterface retryInterface;

    public EventsAdapter(Context context, List<Event> eventList, int color, RetryInterface retryInterface) {
        this.context = context;
        this.eventList = eventList;
        this.color = color;
        this.retryInterface = retryInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == EVENTS_ITEMS)
            return new EventsAdapter.EventViewHolder(lf.inflate(R.layout.item_event, parent, false));
        else if (viewType == LOADING_ITEM)
            return new EventsAdapter.EventViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        else if (viewType == ERROR_ITEM)
            return new ErrorViewHolder(lf.inflate(R.layout.item_error_message, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == EVENTS_ITEMS) {
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
                    .load(ServerApi.getImgUrl(event.getImage(), true))
                    .placeholder(R.drawable.ic_big_placeholder)
                    .fit()
                    .centerInside()
                    .transform(new RoundedCornersTransformation((int) (4 * dpi), 0, RoundedCornersTransformation.CornerType.TOP))
                    .into(((EventViewHolder) holder).image);

            ((EventViewHolder) holder).title.setText(event.getTitle());
            ((EventViewHolder) holder).timestamp.setText(event.getTimestamp());
            ((EventViewHolder) holder).description.setText(event.getDescription());
        }
        if (getItemViewType(position) == ERROR_ITEM) {
            ((ErrorViewHolder) holder).retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ((ErrorViewHolder) holder).retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryInterface.retry();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position != getItemCount() - 1)
            return EVENTS_ITEMS;
        else {
            if (state == ERROR)
                return ERROR_ITEM;
            else if (state == LOADING)
                return LOADING_ITEM;
            else
                return EVENTS_ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        if (state == DEFAULT)
            return eventList.size();
        else
            return eventList.size() + 1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    protected class EventViewHolder extends RecyclerView.ViewHolder {

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
