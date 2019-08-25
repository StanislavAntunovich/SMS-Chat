package ru.geekbrains.smschat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.smschat.adapters.MessagesAdapter;
import ru.geekbrains.smschat.data.DataBasePresenter;
import ru.geekbrains.smschat.database.ChatEntity;
import ru.geekbrains.smschat.database.MessageEntity;
import ru.geekbrains.smschat.database.SMSDatabase;

import static ru.geekbrains.smschat.MainActivity.permissionSmsRequestCode;

public class ChatActivity extends AppCompatActivity {

    private List<MessageEntity> messages;
    private MessagesAdapter adapter;
    private SmsReceiver receiver;
    private String currentPhone;

    private ImageButton btnSend;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setData();
        initViews();
        setListener();

        ActionBar toolbar = getSupportActionBar();

        if (toolbar != null) {
            toolbar.setTitle(currentPhone);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        setRecycler();
        setBroadcastReceiver();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void setData() {
        Intent intent = getIntent();
        currentPhone = intent.getStringExtra(MainActivity.PHONE);
        SMSDatabase database = DataBasePresenter.getInstance().getDatabase();
        messages = database.getMessageDao().getMessagesByChat(currentPhone);
    }

    private void setListener() {
        btnSend.setOnClickListener(v -> {
            String message = editMessage.getText().toString();
            if (message.isEmpty()) {
                showToast(getString(R.string.warning_fill_message));
            } else {
                sendMessage(message);
            }
        });
    }

    private void initViews() {
        btnSend = findViewById(R.id.btn_send);
        editMessage = findViewById(R.id.inp_message);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    private void sendMessage(String message) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.SEND_SMS};
            showToast(getString(R.string.warning_no_permission_to_send));
            ActivityCompat.requestPermissions(this, permissions, permissionSmsRequestCode);
        } else {
            SmsManager.getDefault()
                    .sendTextMessage(currentPhone, null, message, null, null);
            SMSDatabase database = DataBasePresenter.getInstance().getDatabase();
            MessageEntity messageEntity = new MessageEntity(currentPhone, message, false);
            database.getMessageDao().insert(messageEntity);
            messages.add(messageEntity);
            adapter.notifyDataSetChanged();
            editMessage.setText("");
        }
    }

    private void setRecycler() {
        RecyclerView recyclerView = findViewById(R.id.rcv_messages);
        adapter = new MessagesAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setBroadcastReceiver() {
        receiver = new SmsReceiver();

        receiver.setListener(message -> {
            SMSDatabase database = DataBasePresenter.getInstance().getDatabase();
            String phone = message.getChatId();
            boolean isRead = false;
            if (currentPhone.equals(phone)) {
                isRead = true;
                messages.add(message);
                adapter.notifyDataSetChanged();
            }

            ChatEntity chatEntity = database.getChatDao().getChatByPhone(phone);
            if (chatEntity == null) {
                chatEntity = new ChatEntity(phone, isRead);
                database.getChatDao().insert(chatEntity);
            } else {
                chatEntity.setRead(isRead);
                database.getChatDao().update(chatEntity);
            }

            database.getMessageDao().insert(message);
        });
    }
}
