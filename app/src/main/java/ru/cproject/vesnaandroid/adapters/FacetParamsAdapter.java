package ru.cproject.vesnaandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.categories.FilterActivity;

/**
 * Created by Bitizen on 29.11.16.
 */

public class FacetParamsAdapter extends RecyclerView.Adapter {

    private FilterActivity context;
    private List<String> facetParams;
    private String facet;

    public FacetParamsAdapter(FilterActivity context, List<String> facetParams, String facet) {
        this.context = context;
        this.facetParams = facetParams;
        this.facet = facet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new FacetParamViewHolder(lf.inflate(R.layout.item_simple_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((FacetParamViewHolder) holder).title.setText(facetParams.get(position));
        ((FacetParamViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.setParam(facet, facetParams.get(position));
                context.stepBack();
            }
        });
    }

    @Override
    public int getItemCount() {
        return facetParams.size();
    }

    private class FacetParamViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        TextView title;

        public FacetParamViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
