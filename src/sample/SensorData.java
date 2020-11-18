package sample;

public class SensorData {
    private Object value;
    private String date;

    public SensorData() {}

    public SensorData(Object value, String date) {
        this.value = value;
        this.date = date;
    }
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }melhor fazer um

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
