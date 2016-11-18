package ru.cproject.vesnaandroid.helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Bitizen on 28.10.16.
 */

public class TabBar {

    private TextView[] tabs;
    private View[] contentLayouts;
    private ViewGroup contentFrame;

    private int activeColor;
    private int disabledColor;

    private int currentTab;

    public TabBar(TextView[] tabs, View[] contentLayouts, ViewGroup contentFrame, int activeColor, int disabledColor)  {
        if (tabs.length != contentLayouts.length) {
            throw new IllegalArgumentException("Tabs count and content count can't be different");
        }
        this.tabs = tabs;
        this.contentLayouts = contentLayouts;
        this.contentFrame = contentFrame;
        this.activeColor = activeColor;
        this.disabledColor = disabledColor;

        this.currentTab = 0;
        this.tabs[0].setTextColor(activeColor);
        this.contentFrame.removeAllViews();
        this.contentFrame.addView(this.contentLayouts[0]);
        for (int i = 1; i < tabs.length; i++) {
            tabs[i].setTextColor(disabledColor);
        }

        for (int i = 0; i < tabs.length; i++) {
            final int finalI = i;
            tabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentTab(finalI);
                }
            });
        }
    }

    public int getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(int activeColor) {
        this.activeColor = activeColor;
        tabs[currentTab].setText(this.activeColor);
    }

    public int getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
        for (int i = 0; i < tabs.length; i++) {
            if (i != currentTab)
                tabs[i].setTextColor(this.disabledColor);
        }
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int currentTab) {
        if (currentTab >= tabs.length )
            throw new IllegalArgumentException("Position is too big");
        if (currentTab == this.currentTab)
            return;
        this.currentTab = currentTab;
        this.contentFrame.removeAllViews();
        this.contentFrame.addView(this.contentLayouts[this.currentTab]);
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setTextColor(disabledColor);
        }
        this.tabs[this.currentTab].setTextColor(activeColor);

    }
}
