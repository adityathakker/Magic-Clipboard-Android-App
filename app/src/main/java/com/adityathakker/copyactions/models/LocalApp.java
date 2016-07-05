package com.adityathakker.copyactions.models;

import android.graphics.drawable.Drawable;

/**
 * Created by adityajthakker on 1/7/16.
 */
public class LocalApp extends Object {
    private String packageName, localName, activityName;
    private Drawable icon;
    private Boolean enabled;

    public LocalApp(String packageName, String localName, String activityName, Drawable icon, Boolean enabled) {
        this.packageName = packageName;
        this.localName = localName;
        this.activityName = activityName;
        this.icon = icon;
        this.enabled = enabled;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLocalName() {
        return localName;
    }

    public String getActivityName() {
        return activityName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
