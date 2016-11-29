package ru.cproject.vesnaandroid.obj;

/**
 * Created by Bitizen on 28.11.16.
 */

public class Facet {

    private String facet;
    private String displayName;

    public Facet(String facet, String displayName) {
        this.facet = facet;
        this.displayName = displayName;
    }

    public String getFacet() {
        return facet;
    }

    public void setFacet(String facet) {
        this.facet = facet;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
