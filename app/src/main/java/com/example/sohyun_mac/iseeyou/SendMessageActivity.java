package com.example.sohyun_mac.iseeyou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendMessageActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{
    private final int MY_PERMISSION_REQUEST_READ_SMS = 0;
    String sendMessageText;
    NfcAdapter mNfcAdapter;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readSMSMessage();
        textView = (TextView)findViewById(R.id.textView_send);
        setContentView(R.layout.activity_send_message);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(SendMessageActivity.this);
        if(mNfcAdapter==null){
            Toast.makeText(SendMessageActivity.this,"NFC is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            Toast.makeText(SendMessageActivity.this,"NFC is avilable", Toast.LENGTH_LONG).show();
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this);
//        mNfcAdapter.setOnNdefPushCompleteCallback(this ,this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = new NdefMessage(new NdefRecord[]{createMimeRecord(sendMessageText, Locale.KOREAN)});
        Log.e("create", "Create Ndef Message");
        return msg;
    }

    public NdefRecord createMimeRecord(String text, Locale locale){
        byte[] data = byteEncoding(text, locale);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    public byte[] byteEncoding(String text, Locale locale){
        // 언어 지정 코드 생성
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // 인코딩 형식 생성
        Charset utfEncoding = Charset.forName("UTF-8");
        // 텍스트를 byte 배열로 변환
        byte[] textBytes = text.getBytes(utfEncoding);

        // 전송할 버퍼 생성
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)langBytes.length;
        // 버퍼에 언어 코드 저장
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        // 버퍼에 데이터 저장
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return data;
    }

    public void readSMSMessage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_SMS)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS}, MY_PERMISSION_REQUEST_READ_SMS );
                Toast.makeText(SendMessageActivity.this,"1", Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSION_REQUEST_READ_SMS);
                Toast.makeText(SendMessageActivity.this,"2", Toast.LENGTH_LONG).show();
            }
        }
        Uri allMesseage = Uri.parse("content://sms");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(allMesseage, new String[]{"_id","thread_id","address","person","date","body"},
                null, null, "date DESC");

        if(cursor != null) {
            cursor.moveToFirst();
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
            Date date = new Date(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(date);
            messageItem.setTime(String.valueOf(dateString));

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
            Toast.makeText(SendMessageActivity.this,sendMessageText, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST_READ_SMS:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(SendMessageActivity.this,"10", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(SendMessageActivity.this,"동의눌러라잉", Toast.LENGTH_LONG).show();
                    finish();
                }
                return ;
            }

        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        // 핸들러에 메시지를 전달한다
        mHandler.obtainMessage(1).sendToTarget();
    }

    // NDEF 메시지 전송이 완료되면 TextView 에 결과를 표시한다
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        final String text = "NDEF message sending completed";
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
            }
        }
    };

}
