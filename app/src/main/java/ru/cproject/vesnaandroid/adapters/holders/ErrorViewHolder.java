package ru.cproject.vesnaandroid.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.cproject.vesnaandroid.R;

/**
 * Created by andro on 29.11.2016.
 */

public class ErrorViewHolder extends RecyclerView.ViewHolder {

    public Button retry;

    public ErrorViewHolder(View itemView) {
        super(itemView);
        retry = (Button) itemView.findViewById(R.id.retry);
    }
}
