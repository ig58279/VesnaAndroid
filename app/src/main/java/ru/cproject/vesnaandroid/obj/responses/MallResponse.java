package ru.cproject.vesnaandroid.obj.responses;

import java.util.List;

import ru.cproject.vesnaandroid.obj.Show;
import ru.cproject.vesnaandroid.obj.mall.Function;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;

/**
 * Created by Bitizen on 04.11.16.
 */

public class MallResponse {

    private MallInfo mallInfo;
    private List<Function> functional;
    private List<Show> shows;

    public MallInfo getMallInfo() {
        return mallInfo;
    }

    public void setMallInfo(MallInfo mallInfo) {
        this.mallInfo = mallInfo;
    }

    public List<Function> getFunctional() {
        return functional;
    }

    public void setFunctional(List<Function> functional) {
        this.functional = functional;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}
