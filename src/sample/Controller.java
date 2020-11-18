package sample;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Connection;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDateTime;

public class Controller {

    @FXML
    ListView localList;

    @FXML
    ListView remoteList;

    ObservableList observableLocalList = FXCollections.observableArrayList();
    ObservableList observableRemoteList = FXCollections.observableArrayList();

    RemoteTemperatureService remoteService = new RemoteTemperatureService();

    ObjectMapper mapper = new ObjectMapper();

    Publisher pub;
    Resource salaP;

    InterSCityService interSCityService = InterSCityService.getInstance();

    @FXML
    public void initialize() {

        EventBus.getDefault().register(this);

        localList.setItems(observableLocalList);
        remoteList.setItems(observableRemoteList);

        salaP = interSCityService.getResource("da305579-9f8c-4c87-93bd-186638b86581");

        initCDDL();

        EventBus.getDefault().post(new InitTemperatureService());

    }


    private void initCDDL() {
        Connection con = ConnectionFactory.createConnection();
        con.setClientId("lcmuniz2@gmail.com");
        con.setHost("broker.mqttdashboard.com");
        con.connect();

        CDDL cddl = CDDL.getInstance();
        cddl.setConnection(con);
        cddl.startService();

        pub = PublisherFactory.createPublisher();
        pub.addConnection(con);

        Subscriber sub = SubscriberFactory.createSubscriber();
        sub.addConnection(con);
        sub.subscribeServiceByName("LocalTemperature");
        sub.setSubscriberListener(message -> {
            Temperature temperature = mapper.convertValue(message.getServiceValue()[0], Temperature.class);
            EventBus.getDefault().postSticky(new NewLocalTemperature(temperature));

            Temperature temperature1 = new Temperature();
            temperature1.setValue(temperature.getValue()+100);
            temperature1.setDate(temperature.getDate());
            SensorData sensorData = new SensorData();
            sensorData.setValue(temperature1);
            sensorData.setDate(LocalDateTime.now().toString());
            interSCityService.publishSensorData(salaP.getUuid(), "temperatureABC", sensorData);
        });
    }

    @Subscribe
    public void on(Message message) {
        if (pub == null) return;
        pub.publish(message);
    }

    @Subscribe
    public void on(NewLocalTemperature event) {
        Platform.runLater(() -> observableLocalList.add(event.getTemperature()));
    }

    @Subscribe
    public void on(NewRemoteTemperature event) {
        Platform.runLater(() -> observableRemoteList.add(event.getTemperature()));
    }

}
