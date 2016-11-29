package ru.cproject.vesnaandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.categories.FilterActivity;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Bitizen on 24.11.16.
 */

public class FilterCategoriesAdapter extends Adapter {

    private FilterActivity context;
    private List<String> categories;

    public FilterCategoriesAdapter(FilterActivity context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new CategoryViewHolder(lf.inflate(R.layout.item_simple_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ((CategoryViewHolder) holder).title.setText(categories.get(position));
        ((CategoryViewHolder) holder).wrapper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.openFacetChose(categories.get(position));
                context.setMain(categories.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private class CategoryViewHolder extends ViewHolder {

        ViewGroup wrapper;
        TextView title;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
