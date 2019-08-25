package ru.geekbrains.smschat.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = ChatEntity.class,
        parentColumns = "phone",
        childColumns = "chatId"
))
public class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private boolean isIncome;
    private String text;

    private String chatId;

    public MessageEntity(String chatId, String text, boolean isIncome) {
        this.chatId = chatId;
        this.text = text;
        this.isIncome = isIncome;
    }

    public MessageEntity() {}

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
