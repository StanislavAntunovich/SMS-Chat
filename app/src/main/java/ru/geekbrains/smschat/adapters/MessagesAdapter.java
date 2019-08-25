package ru.geekbrains.smschat.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.smschat.R;
import ru.geekbrains.smschat.database.MessageEntity;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<MessageEntity> messages;

    public MessagesAdapter(List<MessageEntity> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageEntity messageEntity = messages.get(position);
        String message = messageEntity.getText();
        holder.txtMessage.setText(message);
        int gravity = messageEntity.isIncome() ? Gravity.START : Gravity.END;
        holder.txtMessage.setGravity(gravity);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
        }
    }
}
