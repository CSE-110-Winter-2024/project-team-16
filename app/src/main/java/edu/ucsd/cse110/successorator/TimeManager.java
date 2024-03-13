package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Store the time (after modification of inc button) of the app
 */
public class TimeManager {
    //private final Context context;
    private SharedPreferences sharedPreferences;
    private long mockedDate;
    private long lastDeletionDate;
    private Calendar calendar;
    private static final int TIME_TO_DELETE = 2;

    /*public TimeManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("time", Context.MODE_PRIVATE);
        this.mockedDate = sharedPreferences.getLong("mockedDate", 0L);
        //this.lastDeletionDate = sharedPreferences.getLong("");
        calendar.setTimeInMillis(mockedDate);
    }*/
    public TimeManager() {}

    public Date getCurrentDate() {
        return new Date();
    }

    public Date incrementDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public Calendar getMockedCalendar() {
        return calendar;
    }

    public void updateExecution() {

    }

    public boolean currentBefore2AM() {
        return calendar.get(Calendar.HOUR_OF_DAY) < TIME_TO_DELETE;
    }

}

