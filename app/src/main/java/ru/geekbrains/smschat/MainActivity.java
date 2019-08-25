package ru.geekbrains.smschat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.geekbrains.smschat.adapters.ChatsAdapter;
import ru.geekbrains.smschat.data.DataBasePresenter;
import ru.geekbrains.smschat.data.DataSource;
import ru.geekbrains.smschat.database.ChatEntity;
import ru.geekbrains.smschat.database.MessageEntity;
import ru.geekbrains.smschat.database.SMSDatabase;

public class MainActivity extends AppCompatActivity {
    public static final String PHONE = "PHONE_NUM";

    private static final int permissionRequestCode = 123;
    public static final int permissionSmsRequestCode = 1234;


    private boolean okToRead = false;
    private boolean okToSend = false;

    private ChatsAdapter chatsAdapter;
    private DataSource dataSource;

    private SmsReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DataBasePresenter.initPresenter(getApplicationContext());

        dataSource = new DataSource();

        initRecycler();
        setBroadcastReceiver();

        FloatingActionButton fab = findViewById(R.id.btn_add);
        fab.setOnClickListener(this::sendMessageDialog);

        requestToRead();
        requestToSend();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerSmsReceiver();
                okToRead = true;
            } else {
                showToast(getString(R.string.warning_no_permission));
            }
        } else if (requestCode == permissionSmsRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                okToSend = true;
            } else {
                showToast(getString(R.string.warning_no_permission));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (okToRead) {
            registerSmsReceiver();
        }
        setData();
        chatsAdapter.notifyDataSetChanged();
    }

    private void setData() {
        List<ChatEntity> chats = DataBasePresenter
                .getInstance().getDatabase().getChatDao().getAllChats();
        dataSource.setAll(chats);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (okToRead) {
            unregisterReceiver(receiver);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    private void requestToRead() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        } else {
            okToRead = true;
        }
    }

    private void requestToSend() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.SEND_SMS};
            ActivityCompat.requestPermissions(this, permissions, permissionSmsRequestCode);
        } else {
            okToSend = true;
        }
    }

    private void registerSmsReceiver() {
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);
    }


    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.rcv_sms_chats);
        chatsAdapter = new ChatsAdapter(dataSource);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(chatsAdapter);

        chatsAdapter.setListener(position -> {
            ChatEntity chatEntity = dataSource.getChat(position);
            String phone = chatEntity.getPhone();
            chatEntity.setRead(true);
            chatsAdapter.notifyDataSetChanged();
            DataBasePresenter.getInstance().getDatabase().getChatDao().update(chatEntity);
            startChatActivity(phone);
        });

    }

    private void sendMessageDialog(View view) {
        if (okToSend) {
            SendMessageDialog dialog = SendMessageDialog.newInstance(this::sendMessage);
            FragmentManager manager = getSupportFragmentManager();
            dialog.show(manager, getString(R.string.label_new_sms));
        } else {
            requestToSend();
        }
    }

    private void sendMessage(String phone, String message) {
        SmsManager.getDefault()
                .sendTextMessage(phone, null, message, null, null);
        SMSDatabase database = DataBasePresenter.getInstance().getDatabase();
        ChatEntity chat = database.getChatDao().getChatByPhone(phone);
        MessageEntity messageEntity = new MessageEntity(phone, message, false);
        if (chat == null) {
            chat = new ChatEntity(phone, true);
            database.getChatDao().insert(chat);
            dataSource.addChat(chat);
        } else {
            chat.setRead(true);
            database.getChatDao().update(chat);
        }
        database.getMessageDao().insert(messageEntity);
        chatsAdapter.notifyDataSetChanged();
        startChatActivity(phone);
    }

    private void startChatActivity(String phone) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(PHONE, phone);
        startActivity(intent);
    }

    private void setBroadcastReceiver() {
        receiver = new SmsReceiver();

        receiver.setListener(message -> {
            SMSDatabase database = DataBasePresenter.getInstance().getDatabase();
            String phone = message.getChatId();
            ChatEntity chat = dataSource.getChat(phone);
            if (chat == null) {
                chat = new ChatEntity(phone, false);
                database.getChatDao().insert(chat);
                dataSource.addChat(chat);
            } else {
                chat.setRead(false);
                database.getChatDao().update(chat);
            }
            database.getMessageDao().insert(message);
            chatsAdapter.notifyDataSetChanged();
        });
    }

}
