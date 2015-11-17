package cn.hackingwu.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import cn.hackingwu.service.SpringContextHolder;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class JedisLoadingCache<K,V> implements LoadingCache<K,V>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CacheLoader cacheLoader;
    private JedisCacheBuilder cacheBuilder;
    private ValueOperations operations;

    public JedisLoadingCache(JedisCacheBuilder cacheBuilder) {
        this.cacheBuilder = cacheBuilder;

        operations = cacheBuilder.getTemplate().opsForValue();
    }

    public JedisLoadingCache(CacheLoader cacheLoader, JedisCacheBuilder cacheBuilder) {
        this.cacheLoader = cacheLoader;
        this.cacheBuilder = cacheBuilder;

        operations = cacheBuilder.getTemplate().opsForValue();
    }


    @Override
    public Object get(Object o) throws ExecutionException {
        Object value = getFromRedis(o);
        if (value == null){
            try {
                value = cacheLoader.load(o);
                if (value != null){
                    put(o,value);
                }
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
        return value;
    }

    private Object getFromRedis(Object o){
        String key = getKey(o);
        byte[] redisResult = (byte[])operations.get(key);
        Object value = null;
        if (redisResult != null){
            if (cacheBuilder.getRedisJsonConverter() != null){
                value = cacheBuilder.getRedisJsonConverter().convert(redisResult);
            }else{
                value = JSON.parse(redisResult);
            }
        }
        return value;
    }


    @Override
    public Object getUnchecked(Object o) {
        Object value = null;
        try{
            value = get(o);
        }catch (ExecutionException e){

        }
        return value;
    }

    @Override
    public ImmutableMap getAll(Iterable iterable) throws ExecutionException {
        return null;
    }

    @Override
    public Object apply(Object o) {
        return null;
    }

    @Override
    public void refresh(Object o) {

    }

    @Override
    public V getIfPresent(Object o) {
        return null;
    }

    @Override
    public Object get(Object o, Callable callable) throws ExecutionException {
        Object value = getFromRedis(o);
        if (value == null){
            try {
                value = callable.call();
                put(o,value);
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        operations.set(getKey(key),value,cacheBuilder.getExpireTime().getTime(),cacheBuilder.getExpireTime().getTimeUnit());
    }

    @Override
    public void putAll(Map map) {

    }

    @Override
    public void invalidate(Object o) {
        operations.getOperations().delete(getKey(o));
    }

    @Override
    public void invalidateAll() {
        RedisScript redisScript = (RedisScript) SpringContextHolder.getBean("invalidateAllRedisScript");
        cacheBuilder.getTemplate().execute(redisScript, Collections.singletonList(getKey("*")));
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public CacheStats stats() {
        return null;
    }

    @Override
    public void invalidateAll(Iterable iterable) {

    }

    @Override
    public ImmutableMap getAllPresent(Iterable iterable) {
        return null;
    }

    @Override
    public ConcurrentMap asMap() {
        return null;
    }

    @Override
    public void cleanUp() {

    }

    private String getKey(Object key){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(cacheBuilder.getParentKey());
        stringBuffer.append(":");
        stringBuffer.append(key.toString());
        return stringBuffer.toString();
    }

}
