package edu.ucsd.cse110.successorator;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.IGoalRepository;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private IGoalRepository goalRepository;
    private SharedPreferences mockedDate;
    private MutableLiveData<Long> mockedDateLive = new MutableLiveData<>();
    private static final int TIME_TO_DELETE = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        //this.dataSource = InMemoryDataSource.fromDefault();
        //this.goalRepository = new GoalRepository(dataSource);
        var database = Room.databaseBuilder(
                getApplicationContext(),
                SuccessoratorDatabase.class,
                "successorator-database"
            )
                .allowMainThreadQueries()
                .build();

        this.goalRepository = new RoomGoalRepository(database.goalDao());

        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if(isFirstRun && database.goalDao().count() == 0) {
            goalRepository.save(InMemoryDataSource.TEST_GOALS);

            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }

        mockedDate = getSharedPreferences("mockedDate", MODE_PRIVATE);
        mockedDateLive.setValue(mockedDate.getLong("mockedTime", 0L));

        mockedDate.registerOnSharedPreferenceChangeListener((sharedPrefs, key) -> {
            if ("mockedTime".equals(key)) {
                mockedDateLive.postValue(sharedPrefs.getLong(key, 0L));
                deleteCrossedGoals();
            }
        });

        if (deleteCrossedGoalsNotExecutedToday()) {
            deleteCrossedGoals();
        }
    }

    public IGoalRepository getGoalRepository() {
        return goalRepository;
    }

    private LocalDate calendarToDate(Calendar calendar) {
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private Calendar getExecutedCalendar() {
        long executedTime = mockedDate.getLong("lastExecution", 0L);
        Calendar lastExecutionCalendar = Calendar.getInstance();
        lastExecutionCalendar.setTimeInMillis(executedTime);
        return lastExecutionCalendar;
    }

    private Calendar getMockedCalendar() {
        //long mockedTime = mockedDate.getLong("modifiedTime", 0L);
        long mockedTime = mockedDateLive.getValue();
        Calendar mockedCalendar = Calendar.getInstance();
        mockedCalendar.setTimeInMillis(mockedTime);
        return mockedCalendar;
    }

    private boolean executedBefore2AM() {
        return getExecutedCalendar().get(Calendar.HOUR_OF_DAY) < TIME_TO_DELETE;
    }

    private boolean mockedBefore2AM() {
        return getMockedCalendar().get(Calendar.HOUR_OF_DAY) < TIME_TO_DELETE;
    }

    private boolean executedDateBefore() {
        return calendarToDate(getExecutedCalendar()).isBefore(calendarToDate(getMockedCalendar()));
    }

    private long executedDaysBefore() {
        return ChronoUnit.DAYS
                .between(calendarToDate(getExecutedCalendar()), calendarToDate(getMockedCalendar()));
    }

    public boolean deleteCrossedGoalsNotExecutedToday() {
        long executedTime = mockedDate.getLong("lastExecution", 0L);
        Calendar lastExecutionCalendar = Calendar.getInstance();
        lastExecutionCalendar.setTimeInMillis(executedTime);
        long mockedTime = mockedDate.getLong("mockedTime", 0L);
        Calendar mockedCalendar = Calendar.getInstance();
        mockedCalendar.setTimeInMillis(mockedTime);
        if (calendarToDate(lastExecutionCalendar).isBefore(calendarToDate(mockedCalendar))) {
            return true;
        }
//        if (executedBefore2AM()) {
//            return !mockedBefore2AM() || executedDateBefore();
//        } else {
//            return (mockedBefore2AM() && (executedDaysBefore() > 1)) ||
//                    (!mockedBefore2AM() && executedDateBefore());
//        }
        return false;
    }

    private void deleteCrossedGoals() {
        goalRepository.deleteCrossedGoals();

        //Update the record of last deletion execution
        long mockedTime = mockedDate.getLong("mockedTime", 0L);
        mockedDate.edit().putLong("lastExecution", mockedTime).apply();
    }
}
