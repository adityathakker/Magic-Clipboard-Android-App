package com.adityathakker.magicclipboard.events;

/**
 * Created by Aditya Thakker (Github: @adityathakker) on 20/11/16.
 */

public class ClipboardLogFavoriteChangeEvent {
    private int position;
    private Boolean isFav;
    private String source;

    public ClipboardLogFavoriteChangeEvent(int position, Boolean isFav, String source) {
        this.position = position;
        this.isFav = isFav;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public int getPosition() {
        return position;
    }

    public Boolean getFav() {
        return isFav;
    }
}
