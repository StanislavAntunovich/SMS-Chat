package ru.geekbrains.smschat.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import ru.geekbrains.smschat.database.SMSDatabase;

public class DataBasePresenter {
    private static final String DB_NAME = "SMSDatabase";

    private static DataBasePresenter instance;

    private SMSDatabase database;

    private DataBasePresenter(@NonNull Context context) {
        database = Room.databaseBuilder(context, SMSDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public static void initPresenter(Context context) {
        if (instance == null) {
            instance = new DataBasePresenter(context);
        }
    }

    public static DataBasePresenter getInstance() {
        return instance;
    }

    public SMSDatabase getDatabase() {
        return database;
    }
}
