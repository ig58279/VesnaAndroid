package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.obj.Category;

/**
 * Created by Bitizen on 09.11.16.
 */

public class TagsViewHelper {

    public static void fillTagsView(final LinearLayout tagView, final List<Category> tagsList) {
        ViewTreeObserver viewTreeObserver = tagView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tagView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = tagView.getWidth();
                    LayoutInflater lf = LayoutInflater.from(tagView.getContext());

                    int currentWidth = 0;
                    LinearLayout row = new LinearLayout(tagView.getContext());
                    row.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    for (int i = 0; i < tagsList.size(); i++) {
                        TextView tag = (TextView) lf.inflate(R.layout.item_tag, row, false);
                        tag.setText(tagsList.get(i).getName());
                        if (tag.getMeasuredWidth() + currentWidth <= width) {
                            currentWidth += tag.getMeasuredWidth();
                        } else {
                            tagView.addView(row);
                            row = new LinearLayout(tagView.getContext());
                            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            currentWidth = tag.getMeasuredWidth();
                        }
                        row.addView(tag);
                    }
                    tagView.requestLayout();
                }
            });
        }

    }
}
