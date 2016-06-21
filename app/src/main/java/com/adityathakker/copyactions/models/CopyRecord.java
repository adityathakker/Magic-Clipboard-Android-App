package com.adityathakker.copyactions.models;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class CopyRecord {
    private long id;
    private String string;
    private Date timestamp;
    private Boolean isFav;

    public CopyRecord(long id, String string, Date timestamp, Boolean isFav) {
        this.id = id;
        this.string = string;
        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            timestamp = format.parse(timestampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        this.timestamp = timestamp;
        this.isFav = isFav;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "CopyRecord:{\"ID\":" + getId() + ", \"String\": " + getString() + " \"TimeStamp\": " + new Timestamp(timestamp.getTime()).toString() + ", \"isFav\": " + isFav.toString() + " }";
    }

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }
}
