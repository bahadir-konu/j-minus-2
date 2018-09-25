package program.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class MemorySpace {
    private String name;
    private Map<String, Object> members = new HashMap<String, Object>();

    public MemorySpace(String name) {
        this.name = name;
    }

    public Object get(String id) {
        return members.get(id);
    }

    public boolean exists(String memberKey) {
        return members.keySet().contains(memberKey);
    }

    public void put(String id, Object value) {
        members.put(id, value);
    }

    public String toString() {
        return name + ":" + members;
    }
}
