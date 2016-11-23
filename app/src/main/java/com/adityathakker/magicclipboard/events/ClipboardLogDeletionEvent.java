package com.adityathakker.magicclipboard.events;

/**
 * Created by Aditya Thakker (Github: @adityathakker) on 20/11/16.
 */

public class ClipboardLogDeletionEvent{
    private int position;

    public int getPosition() {
        return position;
    }

    public ClipboardLogDeletionEvent(int position) {

        this.position = position;
    }
}
