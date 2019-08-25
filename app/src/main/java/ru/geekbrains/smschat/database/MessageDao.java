package ru.geekbrains.smschat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM MessageEntity WHERE chatId IS :phone")
    List<MessageEntity> getMessagesByChat(String phone);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MessageEntity entity);
}
