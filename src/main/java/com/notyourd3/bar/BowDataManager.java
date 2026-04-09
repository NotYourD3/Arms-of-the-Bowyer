package com.notyourd3.bar;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BowDataManager {
    private static BowDataManager instance;
    private Map<String,BowData> bowDataMap = new HashMap<>();
    public static BowDataManager getInstance(){
        if (instance == null){
            instance = new BowDataManager();
        }
        return instance;
    }
    public boolean containsKey(String key){
        return bowDataMap.containsKey(key);
    }
    public BowData getBowData(String name){
        return bowDataMap.get(name);
    }
    public void putBowData(String[] bowDataRaw){
        Stream.of(bowDataRaw).map(raw -> raw.split("\\|")).forEach(parts -> {
            String id = parts[0];
            BowData data = new BowData(parts);
            bowDataMap.put(id,data);
        });
    }
}
