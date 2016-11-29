package ru.cproject.vesnaandroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.categories.FilterActivity;
import ru.cproject.vesnaandroid.obj.Facet;

/**
 * Created by Bitizen on 28.11.16.
 */

public class FacetsAdapter extends RecyclerView.Adapter {

    private static final int FACET = 0;
    private static final int RESET = 1;

    private FilterActivity context;
    private List<Facet> facets;
    private Map<String, String> chosenParams;

    public FacetsAdapter(FilterActivity context, List<Facet> facets, Map<String, String> chosenParams) {
        this.context = context;
        this.facets = facets;
        this.chosenParams = chosenParams;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == FACET)
            return new FacetViewHolder(lf.inflate(R.layout.item_facet, parent, false));
        else if (viewType == RESET)
            return new ResetViewHolder(lf.inflate(R.layout.item_reset, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == FACET) {
            final Facet facet = facets.get(position);
            ((FacetViewHolder) holder).title.setText(facet.getDisplayName());
            if (chosenParams.containsKey(facet.getFacet())) {
                ((FacetViewHolder) holder).facet.setText(chosenParams.get(facet.getFacet()));
                ((FacetViewHolder) holder).facet.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack));
            } else {
                ((FacetViewHolder) holder).facet.setText("Все");
                ((FacetViewHolder) holder).facet.setTextColor(ContextCompat.getColor(context, R.color.colorTextGray));
            }

            ((FacetViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.openFacetParams(facet.getFacet());
                }
            });
        } else if (getItemViewType(position) == RESET) {
            ((ResetViewHolder) holder).reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.setResult(Activity.RESULT_CANCELED);
                    context.finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return facets.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return RESET;
        else
            return FACET;
    }

    private class FacetViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        TextView title;
        TextView facet;

        FacetViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            title = (TextView) itemView.findViewById(R.id.title);
            facet = (TextView) itemView.findViewById(R.id.facet);
        }
    }

    private class ResetViewHolder extends RecyclerView.ViewHolder {

        ViewGroup reset;

        public ResetViewHolder(View itemView) {
            super(itemView);

            reset = (ViewGroup) itemView.findViewById(R.id.reset);
        }
    }
}
