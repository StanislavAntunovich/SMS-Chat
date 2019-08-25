package ru.geekbrains.smschat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SendMessageDialog extends DialogFragment {
    private OnSendMessageListener listener;

    private EditText txtPhone;
    private EditText txtMessage;
    private Button btnOk;
    private Button btnCancel;

    static SendMessageDialog newInstance(OnSendMessageListener listener) {
        return new SendMessageDialog(listener);
    }

    private SendMessageDialog(OnSendMessageListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_message_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListeners();
    }

    private void initView(@NonNull View view) {
        txtPhone = view.findViewById(R.id.edit_phone);
        txtMessage = view.findViewById(R.id.edit_message);
        btnOk = view.findViewById(R.id.btn_ok);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }

    private void clearFields() {
        txtMessage.setText("");
        txtPhone.setText("");
    }

    private void setListeners() {
        btnOk.setOnClickListener(v -> {
            String phone = txtPhone.getText().toString();
            String message = txtMessage.getText().toString();

            if (phone.isEmpty() || message.isEmpty()) {
                Toast.makeText(getContext(), "pls fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            clearFields();
            dismiss();
            listener.onSendClicked(phone, message);
        });
        btnCancel.setOnClickListener(v -> dismiss());
    }


    public interface OnSendMessageListener {
        void onSendClicked(String phone, String message);
    }
}
