package com.adityathakker.magicclipboard.events;

import com.adityathakker.magicclipboard.models.ClipboardLog;

/**
 * Created by Aditya Thakker (Github: @adityathakker) on 20/11/16.
 */

public class ClipboardLogAdditionEvent {
    private ClipboardLog clipboardLog;

    public ClipboardLogAdditionEvent(ClipboardLog clipboardLog) {
        this.clipboardLog = clipboardLog;
    }

    public ClipboardLog getClipboardLog() {
        return clipboardLog;
    }
}
