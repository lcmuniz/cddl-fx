package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.greenrobot.eventbus.EventBus;

public class RemoteTemperatureService {

    InterSCityService service = InterSCityService.getInstance();

    ObjectMapper mapper = new ObjectMapper();

    public RemoteTemperatureService() {
        init();
    }

    private void init() {

        new Thread(() -> {
            while(true) {

                SensorData sensorData = service.getLastSensorData("da305579-9f8c-4c87-93bd-186638b86581", "temperatureABC");
                Temperature temperature = mapper.convertValue(sensorData.getValue(), Temperature.class);
                EventBus.getDefault().post(new NewRemoteTemperature(temperature));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}