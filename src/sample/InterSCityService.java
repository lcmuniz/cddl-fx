package sample;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class InterSCityService {

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String INTERSCITY_URL = "http://api.playground.interscity.org";

    private static InterSCityService instance;

    private InterSCityService() {}

    public static InterSCityService getInstance() {
        if (instance == null) {
            instance = new InterSCityService();
        }
        return instance;
    }

    public Capability getCapability(String name) {
        return rest.getForObject(INTERSCITY_URL + "/catalog/capabilities/" + name, Capability.class);
    }
    public Capability publishCapability(Capability capability) {
        return rest.postForObject(INTERSCITY_URL + "/catalog/capabilities", capability, Capability.class);
    }

    public Resource getResource(String uuid) {
        Map resp = rest.getForObject(INTERSCITY_URL + "/catalog/resources/" + uuid, Map.class);
        return mapper.convertValue(resp.get("data"), Resource.class);
    }
    public Resource publishResource(Resource resource) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("data", resource);
        Map resp = rest.postForObject(INTERSCITY_URL + "/catalog/resources", data, Map.class);
        return mapper.convertValue(resp.get("data"), Resource.class);
    }


    public ArrayList<SensorData> getSensorData(String uuid, String capability) {
        String[] capabilities = new String[]{capability};
        HashMap<Object, Object> body = new HashMap<>();
        body.put("capabilities", capabilities);
        Map resp = rest.postForObject(INTERSCITY_URL + "/collector/resources/" + uuid + "/data", body, Map.class);
        ArrayList resources = (ArrayList) resp.get("resources");
        Map resource = (Map) resources.get(0);
        Map caps = (Map) resource.get("capabilities");
        ArrayList temps = (ArrayList) caps.get(capability);
        ArrayList<SensorData> list = (ArrayList<SensorData>) temps.stream().map(temp -> mapper.convertValue(temp, SensorData.class)).collect(Collectors.toList());
        return list;
    }
    public SensorData getLastSensorData(String uuid, String capability) {
        String[] capabilities = new String[]{capability};
        HashMap<Object, Object> body = new HashMap<>();
        body.put("capabilities", capabilities);
        Map resp = rest.postForObject(INTERSCITY_URL + "/collector/resources/" + uuid + "/data/last", body, Map.class);
        List resources = (ArrayList) resp.get("resources");
        Map resource = (Map) resources.get(0);
        Map caps = (Map) resource.get("capabilities");
        List temps = (ArrayList) caps.get(capability);
        ArrayList<SensorData> list = (ArrayList<SensorData>) temps.stream().map(temp -> mapper.convertValue(temp, SensorData.class)).collect(Collectors.toList());
        return list.get(0);
    }
    public void publishSensorData(String uuid, String capability, SensorData sensorData) {
        List<Object> datas = new ArrayList<>();
        datas.add(sensorData);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("data", datas);
        rest.postForObject(INTERSCITY_URL + "/adaptor/resources/" + uuid + "/data/" + capability, data, Map.class);
    }

}

