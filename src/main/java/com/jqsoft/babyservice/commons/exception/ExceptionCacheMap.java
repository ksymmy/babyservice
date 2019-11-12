package com.jqsoft.babyservice.commons.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public final class ExceptionCacheMap {

    private final static ConcurrentHashMap<String, LinkedList<ExceptionInfo>> cache = new ConcurrentHashMap<>();

    private final static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private ExceptionCacheMap(){}

    public static void put(ExceptionInfo info){
        String dateTime = getTime();
        LinkedList<ExceptionInfo> list = null;
        if(cache.containsKey(dateTime)){
            list = cache.get(dateTime);
            list.add(info);
        }else{
            list = new LinkedList<>();
            list.add(info);
        }
        cache.put(dateTime, list);
    }

    public static boolean containsKey(String key){
        return cache.containsKey(key);
    }

    public static LinkedList<ExceptionInfo> get(String key){
        return cache.get(key);
    }

    public static ConcurrentHashMap<String, LinkedList<ExceptionInfo>> getAllMap(){
        return cache;
    }

    public static void remove(String key){
        cache.remove(key);
    }

    /**
     * 获取当前时间的分钟时间数
     * */
    public static String getTime(){
        return format.format(LocalDateTime.now());
    }

    /**
     * 获取当前时间前N分钟前的分钟时间数
     * */
    public static String getTime(int n){
        if(n <= 0){
            return null;
        }
        return format.format(LocalDateTime.now().minusMinutes(n));
    }

}
