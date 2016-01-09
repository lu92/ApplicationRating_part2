package com.android.applicationrating;

import java.util.Comparator;

public class NameComparator implements Comparator<Item> {

    @Override
    public int compare(Item first, Item second) {
        return first.getAppName().compareTo(second.getAppName());
    }
}
