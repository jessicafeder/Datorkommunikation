package Lab2;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TempSensor {

    TempSensor() {
        Scanner sc = new Scanner(System.in);
        String topic = "sensor/KYH/EG";
        String content = "hej" + (int) (Math.random() * 10 +15) ;
        int qos = 2;
        String broker = "tcp://broker.hivemq.com:1883";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.subscribe(topic, new MqttPostPropertyMessageListener());

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String var1, MqttMessage var2) throws Exception {
            System.out.println(var1 + ": " + var2.toString());
        }
    }

    Timer timer;

    TempSensor(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Time's up!");
            timer.cancel(); //Terminate the timer thread
            new TempSensor();
            new TempSensor(1);
        }
    }

    public static void main(String[] args) {
        new TempSensor(1);
        System.out.println("Task scheduled.");
        new TempSensor();
    }
}
