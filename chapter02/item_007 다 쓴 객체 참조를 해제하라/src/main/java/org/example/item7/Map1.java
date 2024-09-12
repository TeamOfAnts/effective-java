package org.example.item7;

import java.util.HashMap;
import java.util.Map;

public class Map1 {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();

        Integer key1 = 1000;
        Integer key2 = 2000;

        map.put(key1, "value1");
        map.put(key2, "value2");

        key1 = null;

        System.gc();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
