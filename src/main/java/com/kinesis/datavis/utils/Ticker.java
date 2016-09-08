package com.kinesis.datavis.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 05/09/16.
 */
public class Ticker {
    private static volatile String hashKey;
    private static volatile String s3Path;

    public Ticker() {
        updateHashKey();
        updateS3Path();

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

        ses.scheduleAtFixedRate((Runnable) this::updateHashKey, 0, 1, TimeUnit.HOURS);
        ses.scheduleAtFixedRate((Runnable) this::updateS3Path, 0, 1, TimeUnit.MINUTES);
    }
    private static class LazyHolder {
        public static final Ticker INSTANCE = new Ticker();
    }

    public static Ticker getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String hashKey() {
        return hashKey;
    }

    public String hashKey(String audienceId) {
        return hashKey + audienceId;
    }

    public String s3Path() {
        return s3Path;
    }

    private void updateHashKey() {
        LocalDateTime currentDate = LocalDateTime.now(Clock.systemUTC());
        int m = currentDate.getMonthValue();
        int y = currentDate.getYear();
        int d = currentDate.getDayOfMonth();
        int h = currentDate.getHour();

        hashKey = String.valueOf(y) + String.valueOf(m) + String.valueOf(d) + String.valueOf(h);
    }

    private void updateS3Path() {
        LocalDateTime currentDate = LocalDateTime.now(Clock.systemUTC());
        int m = currentDate.getMonthValue();
        int y = currentDate.getYear();
        int d = currentDate.getDayOfMonth();
        int h = currentDate.getHour();
        int mm = currentDate.getMinute();

        s3Path = "/" + String.valueOf(y) + "/" + String.valueOf(m) + "/" +
                String.valueOf(d) + "/" + String.valueOf(h) + "/" + String.valueOf(mm)  + "/";
    }


}
