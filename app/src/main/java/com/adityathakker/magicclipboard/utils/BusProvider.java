package com.adityathakker.magicclipboard.utils;

import com.squareup.otto.Bus;

/**
 * Created by Aditya Thakker (Github: @adityathakker) on 20/11/16.
 */

public final class BusProvider {
    private static final Bus bus = new Bus();

    public static Bus getInstance() {
        return bus;
    }

    private BusProvider(){

    }

}
