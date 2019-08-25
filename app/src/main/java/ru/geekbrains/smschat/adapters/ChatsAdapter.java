package ru.geekbrains.smschat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.smschat.R;
import ru.geekbrains.smschat.data.DataSource;
import ru.geekbrains.smschat.database.ChatEntity;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private DataSource dataSource;
    private OnChatClickListener listener;

    public ChatsAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatEntity chatEntity = dataSource.getChat(position);
        String phone = chatEntity.getPhone();
        holder.phoneTxt.setText(phone);
        holder.newMessageIndicator.setVisibility(chatEntity.isRead() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public void setListener(OnChatClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneTxt;
        private ImageView newMessageIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneTxt = itemView.findViewById(R.id.txt_phone_num);
            newMessageIndicator = itemView.findViewById(R.id.img_new_indicator);

            itemView.setOnClickListener(view ->
                listener.onChatClicked(getAdapterPosition())
            );
        }

    }

    public interface OnChatClickListener {
        void onChatClicked(int position);
    }
}
