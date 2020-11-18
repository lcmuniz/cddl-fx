package sample;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Capability {
    private String name;
    private String description;
    @JsonProperty("capability_type")
    private String capabilityType;

    public Capability() {}

    public Capability(String name, String description, String capabilityType) {
        this.name = name;
        this.description = description;
        this.capabilityType = capabilityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCapabilityType() {
        return capabilityType;
    }

    public void setCapabilityType(String capabilityType) {
        this.capabilityType = capabilityType;
    }
}
