package ru.geekbrains.smschat.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class ChatEntity implements Serializable {

    @PrimaryKey
    @NonNull
    private String phone;
    private boolean isRead;

    public ChatEntity(String phone, boolean isRead) {
        this.phone = phone;
        this.isRead = isRead;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
