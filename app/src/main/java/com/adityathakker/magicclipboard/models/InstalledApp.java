package com.adityathakker.magicclipboard.models;

import android.graphics.drawable.Drawable;

/**
 * Created by adityajthakker on 1/7/16.
 */
public class InstalledApp extends Object {
    private String packageName, localName, activityName;
    private Drawable icon;
    private Boolean enabled;

    public InstalledApp(String packageName, String localName, String activityName, Drawable icon, Boolean enabled) {
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

    public String toString(){
        return "Package: " + packageName + "\nActivity: " + activityName;
    }
}
