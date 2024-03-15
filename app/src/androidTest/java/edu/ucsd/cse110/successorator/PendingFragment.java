package edu.ucsd.cse110.successorator;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PendingFragment {
    private List<Goal> goals;


    public PendingFragment(List<Goal> g) {
        goals = g;
    }

    private void onPositiveButtonClick(String mit, Goal.GoalContext context, String time) {
//        var goal = new Goal(null, mit, -1, false, frequency, currentTime, context, true);
//
//
//        activityModel.append(goal);
//
//        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

//    private Calendar mockedCalendar() {
//        Calendar calendar = Calendar.getInstance();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime localDateTime =  LocalDateTime.parse(mockedDate.getString("mockedTime", "0001-01-01 00:00:00"), formatter);
//        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
//                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
//        return calendar;
//    }
}
