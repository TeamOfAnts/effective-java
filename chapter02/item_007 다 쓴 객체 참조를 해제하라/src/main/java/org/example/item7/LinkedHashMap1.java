package org.example.item7;

import java.util.LinkedHashMap;

public class LinkedHashMap1 {
    public static void main(String[] args) {
        final int MAX_ENTRIES = 5;
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(MAX_ENTRIES + 1, .75F, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Integer> eldest) {
                return size() > MAX_ENTRIES;
            }
        };

        map.put(0, 0);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);
        map.put(5, 5);

        System.out.println(map);
    }
}
