package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

//import androidx.fragment.app.Fragment;

//import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
//import edu.ucsd.cse110.successorator.app.ui.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddGoalDialogFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddPendingGoalDialogFragment;
//
import edu.ucsd.cse110.successorator.ui.dialog.AddRecurringGoalDialogFragment;

public class SimplifiedActivity {
    private String mode;
    private Calendar calendar;

    public SimplifiedActivity(String m, Calendar c) {
        mode = m;
        calendar = c;
    }

    /**
     * Set the date in the format of "Weekday MM/DD"
     *
     * @param calendar the date to be set as title
     * @author Yubing Lin
     */
    private String formatDate(Calendar calendar) {
        //From ChatGPT, formatting the date as designed pattern
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] weekdays = dfs.getWeekdays();
        String weekday = weekdays[calendar.get(Calendar.DAY_OF_WEEK)];

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        String formattedDate = weekday.substring(0, 3) + " " + sdf.format(calendar.getTime());

        if (mode.equals("Tmr ")) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        return formattedDate;
    }

    public String setModeTitle() {
        String title;
        if (mode.equals("Tod ")) {
            title = setTitle(mode+formatDate(calendar));
        } else if (mode.equals("Tmr ")) {
            Calendar tmrCalendar = (Calendar) calendar.clone();
            tmrCalendar.add(Calendar.DAY_OF_MONTH, 1);
            title = setTitle(mode+formatDate(tmrCalendar));
        } else {
            title = setTitle(mode);
        }
        return title;
    }

    public String setTitle(String s) {return s;}

}
