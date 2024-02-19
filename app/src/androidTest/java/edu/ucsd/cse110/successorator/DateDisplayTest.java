package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.Toolbar;
import  android.support.v7.widget.Toolbar;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDisplayTest {
    //ViewInteraction incDate = Espresso.onView(ViewMatchers.withId(R.id.action_bar_increment_date));

//    @Rule
//    public ActivityScenarioRule<MainActivity> activityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Test
//    public void defaultDateTest() {
//        //Generate the current date in format of title
//        LocalDate expectedDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE M/d");
//        String expectedString = expectedDate.format(formatter);
//
//        //Check if default title matches current date
//        Espresso.onView(ViewMatchers.isAssignableFrom(Toolbar.class))
//                .check(matches(withText(expectedString)));
//    }
//
//    @Test
//    public void IncDateOnceTest() {
//
//
//    }
}
