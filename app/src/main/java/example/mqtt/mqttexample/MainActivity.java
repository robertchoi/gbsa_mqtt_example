package example.mqtt.mqttexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MqttTest";
//    private static final String SERVER_URI = "tcp://broker.hivemq.com";
private static final String SERVER_URI = "tcp://iot.amtel.co.kr";
//    private static final String TOPIC = "/gbsa/5g";
    final String TOPIC = "v1/devices/me/telemetry";
    private static final String USER_NAME = "gbsa_test_01";
    private static final String PUB_MSG = "{\"wifi_state\":\"1\"}";

    MqttAndroidClient mMqttAndroidClient;
    TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mTv = findViewById(R.id.textView);

        mMqttAndroidClient = new MqttAndroidClient(this, SERVER_URI, "test01");
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USER_NAME);
            mMqttAndroidClient.connect(options);
            mMqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.i(TAG, "connect to : " + serverURI);
                    mTv.setText("connect to : " + serverURI);
                    subscribe();
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.i(TAG, "connectionLost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish(PUB_MSG);
            }
        });
    }

    private void subscribe() {
        try {
            mMqttAndroidClient.subscribe(TOPIC, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(TAG, "subscribed message : " + new String(message.getPayload()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(String data) {
        MqttMessage message = new MqttMessage(data.getBytes());
        try {
            mMqttAndroidClient.publish(TOPIC, message);
            Log.i (TAG, "publish message : " + data);
            mTv.setText("publish message : " + data);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mMqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
