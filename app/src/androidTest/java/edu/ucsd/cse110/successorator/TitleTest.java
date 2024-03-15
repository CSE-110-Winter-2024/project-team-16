package edu.ucsd.cse110.successorator;

import static junit.framework.TestCase.assertEquals;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.Calendar;

public class TitleTest {
    SimplifiedActivity sa;

    @Test
    public void TodayTitleTest() {
        Calendar calendar = getCalendar();

        sa = new SimplifiedActivity("Tod ", calendar);
        assertEquals("Tod Thu 03/14", sa.setModeTitle());
    }

    @Test
    public void TmrTitleTest() {
        Calendar calendar = getCalendar();

        sa = new SimplifiedActivity("Tmr ", calendar);
        assertEquals("Tmr Fri 03/15", sa.setModeTitle());
    }

    @Test
    public void RecurringTitleTest() {
        Calendar calendar = getCalendar();

        sa = new SimplifiedActivity("Recurring", calendar);
        assertEquals("Recurring", sa.setModeTitle());
    }

    @Test
    public void PendingTitleTest() {
        Calendar calendar = getCalendar();

        sa = new SimplifiedActivity("Pending", calendar);
        assertEquals("Pending", sa.setModeTitle());
    }

    @NonNull
    private static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 14);
        return calendar;
    }
}
