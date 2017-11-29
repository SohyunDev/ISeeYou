package com.example.sohyun_mac.iseeyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_mainGoSend, btn_mainGoRecieve;

        btn_mainGoSend = (Button)findViewById(R.id.btn_GoSend);
        btn_mainGoSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent_goSent = new Intent(MainActivity.this, SendMessageActivity.class);
                startActivity(intent_goSent);
                finish();
            }

        });
        btn_mainGoRecieve = (Button)findViewById(R.id.btn_GoReceive);
        btn_mainGoRecieve.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent_goReceive = new Intent(MainActivity.this, ReceiveMessageActivity.class);
                startActivity(intent_goReceive);
                finish();
            }
        });
    }
}
