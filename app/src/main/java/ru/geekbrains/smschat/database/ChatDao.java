package ru.geekbrains.smschat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM ChatEntity")
    List<ChatEntity> getAllChats();

    @Query("SELECT * FROM ChatEntity WHERE phone IS :phone")
    ChatEntity getChatByPhone(String phone);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ChatEntity chatEntity);

    @Update
    void update(ChatEntity chatEntity);
}
