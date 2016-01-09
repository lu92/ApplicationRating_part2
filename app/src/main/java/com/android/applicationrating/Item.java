package com.android.applicationrating;

import android.content.pm.ApplicationInfo;

class Item
{
    private ApplicationInfo applicationInfo;
    private float ratingStar;
    private String appName;

    public Item(ApplicationInfo applicationInfo, int rating, String appName) {
        this.applicationInfo = applicationInfo;
        this.ratingStar = rating;
        this.appName = appName;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public float getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(float ratingStar) {
        this.ratingStar = ratingStar;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}