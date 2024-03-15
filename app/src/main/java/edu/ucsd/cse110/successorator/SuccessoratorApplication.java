package edu.ucsd.cse110.successorator;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

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
    private MutableLiveData<String> mockedDateLive = new MutableLiveData<>();
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
        mockedDateLive.setValue(mockedDate.getString("mockedTime", "0001-01-01 00:00:00"));

        mockedDate.registerOnSharedPreferenceChangeListener((sharedPrefs, key) -> {
            if ("mockedTime".equals(key)) {
                mockedDateLive.postValue(sharedPrefs.getString(key, "0001-01-01 00:00:00"));
                callDeleteDecision();
            }
        });

        callDeleteDecision();
    }

    private void callDeleteDecision(){
        if (deleteCrossedGoalsNotExecutedToday()) {
            deleteCrossedGoals();
        }
    }

    public IGoalRepository getGoalRepository() {
        return goalRepository;
    }

    private LocalDateTime stringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    private LocalDateTime getExecutedDateTime(){
        String executedTime = mockedDate.getString("lastExecution", "0001-01-01 00:00:00");
        return stringToDateTime(executedTime);
    }

    private LocalDateTime getMockedDateTime(){
        String mockedTime = mockedDateLive.getValue();
        return stringToDateTime(mockedTime);
    }

    private boolean executedBefore2AM(){
        return getExecutedDateTime().getHour() < TIME_TO_DELETE;
    }

    private boolean mockedBefore2AM(){
        return getMockedDateTime().getHour() < TIME_TO_DELETE;
    }

    private boolean executedDateBefore(){
        return getExecutedDateTime().toLocalDate().isBefore(getMockedDateTime().toLocalDate());
    }

    private int executedDaysBefore(){
        return getMockedDateTime().getDayOfYear() - getExecutedDateTime().getDayOfYear();
    }

    public boolean deleteCrossedGoalsNotExecutedToday(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        goalRepository.append(new Goal(1, formatter.format(getMockedDateTime()), 2, false, Goal.Frequency.ONETIME));
//        goalRepository.append(new Goal(8, formatter.format(getExecutedDateTime()), 3, false, Goal.Frequency.ONETIME));

        if (executedBefore2AM()) {
            return !mockedBefore2AM() || executedDateBefore();
        } else {
            return (mockedBefore2AM() && (executedDaysBefore() > 1)) ||
                    (!mockedBefore2AM() && executedDateBefore());
        }
    }

    private void deleteCrossedGoals() {
        goalRepository.deleteCrossedGoals();

        //Update the record of last deletion execution
        String mockedTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");
        mockedDate.edit().putString("lastExecution", mockedTime).apply();
    }
}
