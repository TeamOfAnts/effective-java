package org.example.item7;

import java.util.Map;
import java.util.WeakHashMap;

public class Map2 {
    public static void main(String[] args) {
        Map<Integer, String> map = new WeakHashMap<>();

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
