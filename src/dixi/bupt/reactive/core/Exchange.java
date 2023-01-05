package dixi.bupt.reactive.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chendixi
 * Created on 2023-01-06
 */
public class Exchange {

    public static String NAME = "name";

    private Map<String, Object> attributes = new HashMap<>();

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getName() {
        Object s = this.getAttributes().getOrDefault(NAME, "default");
        if (s instanceof String) {
            return (String) s;
        }
        return s.toString();
    }

    public void setName(String name) {
        this.getAttributes().put(NAME, name);
    }
}
