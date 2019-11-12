package com.jqsoft.babyservice.commons.utils;

import com.jqsoft.babyservice.commons.exception.CacheException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Slf4j
@Component
public class RedisUtils {

    public static Integer tokenExpire;
    @Value("${token-expire:43200}")
    public void setTokenExpire(Integer tokenExpire) {
        RedisUtils.tokenExpire = tokenExpire;
    }

    public static Integer valueExpire;
    @Value("${value-expire:1440}")
    public void setValueExpire(Integer valueExpire) {
        RedisUtils.valueExpire = valueExpire;
    }

    @Resource
    private RedisTemplate redisTemplate;

    public boolean exists(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean setNx(String key, Long expir){
        boolean flag = false;
        try{
            flag = (Boolean)redisTemplate.execute(new RedisCallback() {
                @Nullable
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    return redisConnection.setNX(key.getBytes(),longToByte(expir));
                }
            });
            return flag;
        }catch (Exception e){
            log.error("取缓存异常, key = {}, ex = {}", key, e);
            return flag;
        }
    }

    /**
     * long类型转成byte数组
     */
    private static byte[] longToByte(long number) {
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(number & 0xff).byteValue();// 将最低位保存在最低位 temp = temp
            // >> 8;// 向右移8位
        }
        return b;
    }

    public Long incrBy(String key, long val) {
        Long v = null;
        try {
            v = redisTemplate.opsForValue().increment(key, val);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Long incrBy(String key, String filed, long val) {
        Long v = null;
        try {
            v = redisTemplate.opsForHash().increment(key, filed, val);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Long incr(String key) {
        return this.incrBy(key, 1);
    }

    public Long incr(String key, long val, int interval, TimeUnit unit) {
        Long v = null;
        try {
            v = this.incrBy(key, val);
            if (v == 1 && val > 0) { // val小于0表示回退，所以要忽略
                redisTemplate.expire(key, interval, unit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, int interval) {
        Boolean v = false;
        try {
            redisTemplate.expire(key, interval, TimeUnit.SECONDS);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, int interval, TimeUnit unit) {
        Boolean v = false;
        try {
            redisTemplate.expire(key, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value) {
        Boolean v = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value, int minutes) throws CacheException {
        Boolean v = false;
        try {
            redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
            //redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value, int interval, TimeUnit unit) {
        Boolean v = false;
        try {
            redisTemplate.opsForValue().set(key, value, interval, unit);
            //redisTemplate.expire(key, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, long interval, TimeUnit unit) {
        Boolean v = false;
        try {
            redisTemplate.expire(key, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addAsyn(final String key, final Serializable value) {
        add(key, value);
    }

    @Async
    public void addAsyn(final String key, final Serializable value, final int minutes) {
        add(key, value, minutes);
    }

    @Async
    public void addAsyn(final String key, final Serializable value, final int interval, final TimeUnit unit) {
        add(key, value, interval, unit);
    }

    public Object getAndSet(String key, Serializable value, int interval, TimeUnit unit) {
        Object v = null;
        try {
            v = redisTemplate.opsForValue().getAndSet(key, value);
            if (v != null) {
                redisTemplate.expire(key, interval, unit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addList(String key, Collection<Serializable> values) {
        Boolean v = false;
        try {
            if (values != null && values.size() > 0) {
                redisTemplate.opsForList().leftPushAll(key, values);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> Boolean addList(String key, List<T> values) {
        Boolean v = false;
        try {
            if (values != null && values.size() > 0) {
                Collection datas = values;
                redisTemplate.opsForList().leftPushAll(key, datas);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }


    public <T> Boolean rightPushAll(String key, List<T> values) {
        Boolean v = false;
        try {
            if (values != null && !values.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(key, values);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addList(String key, Collection values, int minutes) {
        Boolean v = false;
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addList(String key, List values, int minutes) {
        Boolean v = false;
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }


    public <T> Boolean listAppend(final String key, T value) {
        Boolean v = false;
        try {
            if (null != value) {
                redisTemplate.opsForList().leftPush(key, value);
            }
            v = true;
        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public <T> void listAppendAsyn(final String key, T value) {
        listAppend(key, value);
    }

    @Async
    public void addListAsyn(final String key, final Collection<Serializable> values) {
        addList(key, values);
    }

    @Async
    public void addListAsyn(final String key, final Collection<Serializable> values, final int minutes) {
        addList(key, values, minutes);
    }

    public Boolean addMap(String key, Map<Object, Object> valueMap) {
        Boolean v = false;
        try {
            if (valueMap != null && valueMap.keySet().size() > 0) {
                redisTemplate.opsForHash().putAll(key, valueMap);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addMap(String key, Map<Object, Object> valueMap, int minutes) {
        Boolean v = false;
        try {
            redisTemplate.opsForHash().putAll(key, valueMap);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addMapAsyn(final String key, final Map<Object, Object> valueMap) {
        addMapAsyn(key, valueMap);
    }

    @Async
    public void addMapAsyn(final String key, final Map<Object, Object> valueMap, final int minutes) {
        addMapAsyn(key, valueMap, minutes);
    }

    public Map<Object, Object> getMap(String key) {
        Map<Object, Object> v = null;
        try {
            v = redisTemplate.opsForHash().entries(key);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addOneToList(String key, Serializable value) {
        Boolean v = false;
        try {
            redisTemplate.opsForList().leftPush(key, value);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addOneToListAsyn(final String key, final Serializable value) {
        addOneToList(key, value);
    }

    public Object get(String key) {
        Object v = null;
        try {
            v = redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            //ex.printStackTrace();
            //remove(key);
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public List<Serializable> getList(String key) {
        List<Serializable> v = null;
        try {
            Long size = redisTemplate.opsForList().size(key);
            v = redisTemplate.opsForList().range(key, 0, size);
        } catch (Exception ex) {
//            remove(key);
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> List<T> getObjectList(String key) {
        List<T> v = null;
        try {
            Long size = redisTemplate.opsForList().size(key);
            v = redisTemplate.opsForList().range(key, 0, size);
        } catch (Exception ex) {
//            remove(key);
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Serializable getListFirstOne(String key) {
        Serializable v = null;
        try {
            List<Serializable> le = redisTemplate.opsForList().range(key, 0, 1);
            if (le != null && le.size() > 0) {
                v = le.get(0);
            }
        } catch (Exception ex) {
//            remove(key);
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Integer getCountLike(String keyPrefix) {
        Integer v = null;
        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                Set<String> matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
                v = matchedCacheKeys.size();
            }
        } catch (Exception ex) {
//            removeLike(keyPrefix);
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return v;
    }

    public Boolean remove(String key) {
        Boolean v = false;
        try {
            redisTemplate.delete(key);
            v = true;
        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new CacheException(ex, ex.getMessage());
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean removeLike(String keyPrefix) {
        Boolean v = false;
        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                Set<String> matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
                for (String cacheKey : matchedCacheKeys) {
                    this.remove(cacheKey);
                }
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return v;
    }

    public Set<String> getAllKeyByKeyPrefix(String keyPrefix) {

        Set<String> matchedCacheKeys = null;
        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
            }
        } catch (Exception ex) {
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return matchedCacheKeys;
    }

    public void lpushList(String key, final Serializable value) {

        redisTemplate.opsForList().leftPush(key, value);
    }

    public Object rpopList(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public Long getListSize(String key) {
        Long v = null;
        try {
            v = redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
//            remove(key);
//            ex.printStackTrace();
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public List<Serializable> popList(String key, int offset) {
        List<Serializable> v = null;
        try {
            v = redisTemplate.opsForList().range(key, 0, offset - 1);
            for (Serializable item : v) {
                redisTemplate.opsForList().remove(key, 0, item);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public void hset(String key, String field, Serializable value, int interval, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            if (interval > 0) {
                redisTemplate.expire(key, interval, timeUnit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public void hset(String key, String field, Serializable value, int minutes) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            if (minutes > 0) {
                redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public void hset(String key, String field, Serializable value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return null;
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value,
                         final int interval, final TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, field, value);
        if (interval > 0) {
            redisTemplate.expire(key, interval, timeUnit);
        }
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value, final int minutes) {
        redisTemplate.opsForHash().put(key, field, value);
        if (minutes > 0) {
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
        }
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hgetAsyn(final String key, final String field) {
        redisTemplate.opsForHash().get(key, field);
        return null;
    }

    public Boolean hdel(String key, String field) {
        try {
            Long cnt = redisTemplate.opsForHash().delete(key, field);
            return true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return false;
    }

    public RedisTemplate<String, Serializable> getRedisCacheTemplate() {
        return redisTemplate;
    }

    public void setRedisCacheTemplate(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <V> Boolean zadd(String key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    public <V> Set<V> rangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    public Long zRemove(String key, Object... values) {
        return this.redisTemplate.opsForZSet().remove(key, values);
    }

}
