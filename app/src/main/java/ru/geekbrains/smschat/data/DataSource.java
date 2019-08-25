package ru.geekbrains.smschat.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.smschat.database.ChatEntity;

public class DataSource implements Serializable {

    private List<ChatEntity> chatEntities;

    public DataSource() {
        chatEntities = new ArrayList<>();
    }

    public ChatEntity getChat(String phone) {
        for (ChatEntity chatEntity : chatEntities) {
            if (chatEntity.getPhone().equals(phone)) {
                return chatEntity;
            }
        }
        return null;
    }

    public ChatEntity getChat(int index) {
        return chatEntities.get(index);
    }

    public void addChat(ChatEntity chatEntity) {
        chatEntities.add(chatEntity);
    }

    public void addAll(List<ChatEntity> chats) {
        chatEntities.addAll(chats);
    }

    public void setAll(List<ChatEntity> chats) {
        this.chatEntities = chats;
    }

    public int size() {
        return chatEntities.size();
    }
}
