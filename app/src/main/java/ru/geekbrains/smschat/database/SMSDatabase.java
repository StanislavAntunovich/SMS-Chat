package ru.geekbrains.smschat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ChatEntity.class, MessageEntity.class}, version = 1)
public abstract class SMSDatabase extends RoomDatabase {
    public abstract ChatDao getChatDao();
    public abstract MessageDao getMessageDao();
}
