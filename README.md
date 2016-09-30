## OLYMPUS Android SDK 연동가이드

※ 본 가이드는 OLYMPUS 서비스를 제공하는데 필요한 Agent와의 연동에 목적이 있다. 
 
### 목차
1. OLYMPUS Service 용어 설명 
2. OLYMPUS Service 주의사항 
3. LIB 적용 및 Sample Code

<br>
#### OLYMPUS Service 용어 설명 

| 용어 	       |  의미                                	 					|
| ------------- | ---------------------------------------------------------- |
| 3rd App      	| OLYMPUS Agent와 연동하는 Application						   |
| AgentID(AID)  | OLYMPUS Server로부터 발급받은 Agent의 고유 Key<br> * Agent가 실행되면 내부적으로 발급되는 값 					 |
| AppID 		| 3rd App을 식별할 수 있는 고유 Key<br>* 10자리 Hex 코드 값으로 Feelingk로부터 발급받은 고유값을 사용한다.<br> * 해당 값은 3rd App의 package 이름과 매핑되어 관리 되기 때문에 임의의 값을 사용시 Service 사용 불가하다.|
    
 <br>   
    
#### OLYMPUS Service 주의사항 
* OLYMPUSH Agent의 SDK Min Version은 14(Android 4.0)으로, 이하 버전 지원 프로젝트는 연동이 불가합니다.
* 해당 SDK는 Test용 샘플으로, 실제 OLYMPUS Service 사용을 원할시 하단의 `License` 항목을 참고 하세요.


<br>
    
#### LIB 적용 및 Sample Code
1. OLYMPUS Service를 이용할 프로젝트의 'libs'폴더에 'FLKPushAgentLIB_v1.0.jar'파일을 Import한다.
2. AndroidManifest.xml을 설정한다. 
	* 해당 리시버는 'FLKPushAgentLIB' 내에서 Agent가 설치된 후 연동되는 Flow에 사용되는 규격입니다.
	```xml
    <receiver android:name="com.feelingk.pushagent.lib.FLKPushAgentLibReceiver">
    	<intent-filter>
    		<action android:name="com.feelingk.pushagent.ad.RECEIVED_APP_REG_ID" />
    		<action android:name="com.feelingk.pushagent.ad.RECEIVED_REG_PARAM_ERROR" />
    		<action android:name="com.feelingk.pushagent.ad.RECEIVED_REG_RESULT_ERROR" />
            <!-- android:host의 "testapp"은 임의의 값이며, 개발자가 FLKPushInterface 생성자의 인자값과 동일하게 설정해야 한다. -->
    		<data android:scheme="flk_push" android:host="testapp"/>
    		<category android:name="android.intent.category.DEFAULT" />
    	</intent-filter>
    	<intent-filter>
    		<action android:name="com.feelingk.pushagent.ad.REQUEST_APP_STATE" />
    		<action android:name="com.feelingk.pushagent.ad.REQUEST_READY_FOR_AGENT" />
    	</intent-filter>
    	<intent-filter>
    		<action android:name="android.intent.action.PACKAGE_ADDED" />
    		<data android:scheme="package" />
    	</intent-filter>
    </receiver>
	```

3. 'FLKPushAgentLIB'를 사용할 Activity에 아래와 같이 구현한다.
	```java
	public class ExampleActivity extends AppCompatActivity implements FLKPushInterface.OnPushLibResultListener{

	    private FLKPushInterface flkInterface;

	    @Override
	    protected void onResume() {
    	    super.onResume();

	        if (flkInterface != null) {
        	    flkInterface.interfaceInit();
    	    }
	    }

    	@Override
	    protected void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_example);

			/** 
	         * FLKPushAgentLIB 인터페이스 생성 
        	 * this : Context
    	     * "0000000009" : AppId
    	     * "testapp" : AndroidManifest.xml에 FLKPushAgentLibReceiver에 android:host 기입한 값과 동일하게 설정 
    	     * 	this : FLKPushInterface.OnPushLibResultListener OLYMPUSH 앱 연동 결과를 리턴해준다.
	         */
        	flkInterface = new FLKPushInterface(this, "0000000009", "testapp", this);

    	    Button receiveMessagebox = (Button)findViewById(R.id.btn_received_messagebox);
	        receiveMessagebox.setOnClickListener(new View.OnClickListener() {
        	    @Override
    	        public void onClick(View view) {
                	// Agent 내의 수신함 연동
	                if (flkInterface != null) {
                	    flkInterface.sendToReceiveActivity();
            	    }
        	    }
    	    });

	    }

    	@Override
	    public void onResult(int resultCode) {
    	  // 응답코드에 따른 결과 처리 
	    }
	}
	```

4. 응답 코드

	| Code  |  Message                       | 설명                                                 |
	|:-----:|--------------------------------|-----------------------------------------------------|
	| 700   | User Terms Agree Fail          | 사용자가 OLYMPUS Agent 사용 약관 미동의로 인해 서비스 이용불가  |
	| 993   | Not Ready State                | Agent 서비스 동작 준비중 상태                             |
	| 995   | Parameter Error                | RID Parameter 값 오류           					  |
    | 996   | Fail Connection                | RID 발급 요청에 대한 서버응답 Null로 인한 RID 발급 실패 오류	  |
	| 998   | Push Service License Fail      | Agent Service License Fail  						   |
	| 999   | Service License Fail           | 인증되지 않은 AppID나 Package 사용 오류  				  |
	| 1000  | Linked Success                 | Agent 연동 성공   							 		 |
	| 1001  | User App Install Fail          | 사용자가 OLYMPUS Agent 설치를 거부하여 서비스 이용 불가 	   |



<br>
### Class Reference
<hr/>

|FLKPushInterface |설명  |
|-----------------|------|
| FLKPushInterface|FLKPushAgentLIB 사용을 위한 Interface 생성자<br>Parameters<br> * Context : Activity or Application Context <br> * AppID : Feelingk로부터 발급 받은 App 고유 키값 (App 분별값) <br> * HostName : AndroidManifest.xml에 android:host  기입하는 값과 동일하게 설정<br>* OnPushLibResultListener : FLKPushAgentLIB와의 연동 결과를 전달 받기 위한 리스너 |
|interfaceInit() | Agent와 연동. Agent 연동 결과는 OnPushLibResultListener onResult로 전달 받는다. (`Screenshot Flow` 항목 참고) |
|sendToReceiveActivity()|Agent 내 수신함 연동. <br>interfaceInit() 함수를 통해 onResult에 '1000' 코드 받았을 시에만 정상적으로 화면 연동 가능하다.<br> '1000' 외의 코드 전달 받았을 시 interfaceInit()과 동작 Flow는 동일하다.|


<br>
### Screenshot Flow
<hr/>

* FLKPushAgentLIB 동작 순서

|Sreenshot |Step  |
|-----------------|------|
|<img src="https://cloud.githubusercontent.com/assets/22470636/18987237/b9a53bce-873b-11e6-938b-4282efd940a5.png" width="320" height="400" alt="step1" /> |* Step1. Agent 미설치 시, Google Play 스토어 연동 안내 팝업 노출 <br> - '확인' 버튼 선택 시, Step2의 Google Play의 Agent App 페이지로 이동 <br> - '취소' 버튼 선택 시, '1001' 코드 전달  |
|<img src="https://cloud.githubusercontent.com/assets/22470636/18987236/b99e4210-873b-11e6-9602-575656d209ba.png" width="320" height="400" alt="step2" /> | * Step2. Google Play 스토어 Agent App 페이지 연동<br> - Agent 설치 시, Agent 내부적으로 AgentID 발급되고, Step3 진행 <br>- 설치하지 않고 추후 App 실행 시, Step1 진행 |
|<img src="https://cloud.githubusercontent.com/assets/22470636/18987235/b97fe072-873b-11e6-946b-e1fbd37deb49.png" width="320" height="400" alt="step3" /> | * Step3. '위치 및 마케팅 이용동의' 팝업 노출<br> - '아니오' 버튼 선택 시, 최초 1회는 '700' 코드 전달 되며, 이후 interfaceInit() 함수 호출 시 '1001' 코드로 전달<br>- '예' 버튼 선택 시, Agent 와 연동 결과 코드 전달. 정상 연동시 '1000' 코드 전달|
|<img src="https://cloud.githubusercontent.com/assets/22470636/18988068/e756a086-873f-11e6-95ac-b841f07f6781.jpg" width="320" height="400" alt="step3" />|* Step4. sendToReceiveActivity() 함수 호출 시 Agent 내의 Message Box 연동<br> - Agent와 정상적으로 연동되어 '1000'코드를 받은 후 Step4 진행 시 Message Box 화면으로 연동 된다.<br> - '1000' 외의 코드를 전달 받은 후 Step4 진행 시 Step1 부터 진행된다.|


<br>
### Release Note
<hr/>

- [v1.0](https://github.com/pushfeelingk/OLYMPUS-TestExample/blob/dac6a9731e7cb893a6b155341a6580099920c66d/app/libs/FLKPushAgentLIB_v1.0.jar)
	



<br>
### License 
<hr/>
OLYMPUS Service에 관련하여 문의사항은 아래로 문의 주시면 빠르게 응대해드리도록 하겠습니다.

* <B> Call </B> : 02 - 2102 - 7300
* <B> Email </B> : olympus-push@feelingk.com

<br><br>
Copyright 2016. Feelingk All Right Reserved.

