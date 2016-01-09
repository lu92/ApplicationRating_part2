package com.android.applicationrating;

import android.widget.ListView;

public interface ListenerActivity
{
    void runSelectedApp(int position);
    ListView getListView();
//    void doFilter();
//    void sortByName();
//    void sortByRating();
}
