package com.android.applicationrating;

import java.util.Comparator;

public class RatingComparator implements Comparator<Item> {

    @Override
    public int compare(Item first, Item second) {
        return Float.compare(first.getRatingStar(), second.getRatingStar());
    }
}
