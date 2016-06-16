package com.adityathakker.copyactions.models;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class CopyRecord {
    private long id;
    private String string;
    private Date timestamp;

    public CopyRecord(long id, String string, String timestampString) {
        this.id = id;
        this.string = string;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            timestamp = format.parse(timestampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public String getString() {
        return string;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "CopyRecord:{\"ID\":" + getId() + ", \"String\": " + getString() + " \"TimeStamp\": " + new Timestamp(timestamp.getTime()).toString() + " }";
    }
}
