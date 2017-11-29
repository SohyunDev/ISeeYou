package com.example.sohyun_mac.iseeyou;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class ReceiveMessageActivity extends AppCompatActivity {

    private TextView readText;
    private Button btn_receive;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Tag tag;
    private IsoDep tagcomm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);
        readText = (TextView)findViewById(R.id.readText);
        btn_receive = (Button)findViewById(R.id.btn_receive);
        btn_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNFC();
            }
        });
    }
    public void readNFC(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter==null){
            Toast.makeText(this,"NFC is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            Toast.makeText(this,"NFC is avilable", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this,getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try{
            ndef.addDataType("*/*");
            mFilters = new IntentFilter[]{ndef};
        }
        catch(Exception e){
            throw new RuntimeException("fail", e);
        }
        mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    /*
    @Override
    public void onResume(){
        super.onResume();
        if(mNfcAdapter != null){
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,mFilters, mTechLists);
        }
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            onNewIntent(getIntent());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mNfcAdapter != null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }
    */

    @Override
    public void onNewIntent(Intent intent)
    {
        String action = intent.getAction();
        String tag = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG).toString();
        String strMsg = action + "\n\n"+tag;
        readText.setText(strMsg);

        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(messages == null){
            return;
        }
        for(int i=0; i<messages.length; i++){
            showMsg((NdefMessage)messages[i]);
        }
    }

    public void showMsg(NdefMessage mMessage){
        String strMsg = "", strRec="";
        NdefRecord[] recs = mMessage.getRecords();
        for(int i=0; i<recs.length; i++){
            NdefRecord record = recs[i];
            byte[] payload = record.getPayload();
            if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)){
                strRec = byteDecoding(payload);
                strRec = "Text: " + strRec;
            }
            //TODO :  RTD_TEXT가 아닐 경우 (Send에서 RTD_TEXT)만 해서 상관없을듯

            strMsg+=("\n\nNdefRecord["+i+"]:\n"+strRec);
        }
        readText.append(strMsg);
    }

    public String byteDecoding(byte[] buf){
        String strTxt="";
        String textEncoding=((buf[0]&0200)==0)?"UTF-8":"UTF-16";
        int langCodeLen = buf[0]&0077;

        try{
            strTxt=new String(buf, langCodeLen+1, buf.length-langCodeLen-1,textEncoding);
        }
        catch (Exception e){
            Log.d("tag1",e.toString());
        }
        return strTxt;
    }
}
