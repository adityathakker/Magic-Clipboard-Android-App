package com.adityathakker.magicclipboard.models;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class ClipboardLog {
    private long id;
    private String clip;
    private Date timestamp;
    private Boolean isFav;

    public ClipboardLog(long id, String clip, Date timestamp, Boolean isFav) {
        this.id = id;
        this.clip = clip;
        this.timestamp = timestamp;
        this.isFav = isFav;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClip() {
        return clip;
    }

    public void setClip(String clip) {
        this.clip = clip;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "ClipboardLog:{\"ID\":" + getId() + ", \"String\": " + getClip() + " \"TimeStamp\": " + new Timestamp(timestamp.getTime()).toString() + ", \"isFav\": " + isFav.toString() + " }";
    }

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }
}
