package com.flk.olympus.texample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feelingk.pushagent.lib.FLKPushInterface;

public class ExampleActivity extends AppCompatActivity implements FLKPushInterface.OnPushLibResultListener{
    private TextView mStateTextView;

    private FLKPushInterface flkInterface;

    private Toast mToast;

    @Override
    protected void onResume() {
        super.onResume();

        if(flkInterface != null) {
            flkInterface.interfaceInit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        mStateTextView = (TextView)findViewById(R.id.tv_state);

        flkInterface = new FLKPushInterface(this, "0000000009", "testapp" , this);

        Button receiveMessagebox = (Button)findViewById(R.id.btn_received_messagebox);
        receiveMessagebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flkInterface != null) {
                    flkInterface.sendToReceiveActivity();
                }
            }
        });

    }

    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(ExampleActivity.this, msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

    @Override
    public void onResult(int resultCode) {
        String stateMsg = "";
        if(resultCode == 1000){
            stateMsg = "정상적으로 PushAgent와 연동되었습니다.";
        }

        if(resultCode == 1001){
            stateMsg = "사용자가 PushAgent 설치를 거부하였습니다.";
        }

        if(resultCode == 700){
            stateMsg = "사용자가 위치 정보 수집 및 마케팅 메시지 수신을 거부하였습니다.";
        }

        if(resultCode == 999){
            stateMsg = "등록되지 않은 APPID나 package입니다";
        }

        if(resultCode == 996){
            stateMsg = "네트워크 불안정으로 인해 정상 서비스 불가합니다. 잠시후 다시 시도해주세요";
        }

        mStateTextView.setText("resultCode : " + resultCode +"\nresultMsg : " + stateMsg);
        showToast(stateMsg);
    }
}