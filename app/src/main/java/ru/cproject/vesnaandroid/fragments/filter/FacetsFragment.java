package ru.cproject.vesnaandroid.fragments.filter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.categories.FilterActivity;
import ru.cproject.vesnaandroid.adapters.FacetsAdapter;

/**
 * Created by Bitizen on 24.11.16.
 */

public class FacetsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof FilterActivity) {
            FilterActivity activity = (FilterActivity) getActivity();
            RecyclerView facetsView = (RecyclerView) view.findViewById(R.id.filter_view);
            facetsView.setAdapter(new FacetsAdapter(activity, activity.getFacets(), activity.getChosenParams()));
            facetsView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }
}
