package org.stations;

import java.util.HashMap;
import java.util.Map;

public class Station {

    private String name;
    private String code;
    private Map<Station, Integer> connections;

    public Station(String name, String code) {
        this.name = name;
        this.code = code;
        this.connections = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Map<Station, Integer> getConnections() {
        return connections;
    }

    public void addConnection(Station target, int distance) {
        connections.put(target, distance);
    }
}
