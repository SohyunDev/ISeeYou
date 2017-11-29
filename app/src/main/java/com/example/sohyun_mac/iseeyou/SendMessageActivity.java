package com.example.sohyun_mac.iseeyou;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class SendMessageActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter mNfcAdapter;
    String sendMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Button btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSMSMessage();
                mNfcAdapter = NfcAdapter.getDefaultAdapter(SendMessageActivity.this);
                if(mNfcAdapter==null){
                    Toast.makeText(SendMessageActivity.this,"NFC is not available", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(SendMessageActivity.this,"NFC is avilable", Toast.LENGTH_LONG).show();
                }
                mNfcAdapter.setNdefPushMessageCallback(SendMessageActivity.this, SendMessageActivity.this);
            }
        });
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return new NdefMessage(new NdefRecord[]{createMimeRecord(sendMessageText, Locale.KOREAN, true)});
    }

    public NdefRecord createMimeRecord(String text, Locale locale, boolean encodeInUtf8){
        final byte[] langBytes = locale.getLanguage().getBytes(StandardCharsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? StandardCharsets.UTF_8 : Charset.forName("UTF-16");
        final byte[] textBytes = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1<<7);
        final char status = (char)(utfBit+langBytes.length);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(new byte[]{(byte)status});
            outputStream.write(langBytes);
            outputStream.write(textBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] data = outputStream.toByteArray();
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    public void readSMSMessage(){
        Uri allMesseage = Uri.parse("content://sms");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(allMesseage, new String[]{"_id","thread_id","address","person","date","body"},
                null, null, "date DESC");

        if(cursor != null) {
            MessageItem messageItem = new MessageItem();

            long messageId = cursor.getLong(0);
            messageItem.setMessageId(String.valueOf(messageId));

            long threadId = cursor.getLong(1);
            messageItem.setThreadId(String.valueOf(threadId));

            String phoneNum = cursor.getString(2);
            messageItem.setPhoneNum(phoneNum);

            long contactId = cursor.getLong(3);
            messageItem.setContactId(String.valueOf(contactId));

            String contactId_string = String.valueOf(contactId);
            messageItem.setContactId_string(contactId_string);

            long time = cursor.getLong(4);
            messageItem.setTime(String.valueOf(time));

            String body = cursor.getString(5);
            messageItem.setBody(body);

            cursor.close();

            sendMessageText = "";
            String newLine = "\n";
            String phoneNumText = "전화번호 : ";
            String timeText = "시간 : ";
            String bodyText = "내용 : ";
            sendMessageText += phoneNumText + messageItem.getPhoneNum() + newLine;
            sendMessageText += timeText + messageItem.getTime() + newLine;
            sendMessageText += bodyText + messageItem.getBody() + newLine;
        }
    }
}
