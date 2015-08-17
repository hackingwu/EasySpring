package cache;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class JedisCacheBuilder {

    private RedisTemplate template;

    private ExpireTime expireTime;

    private String parentKey;

    private RedisJsonConverter redisJsonConverter;

    public JedisCacheBuilder(RedisTemplate template,String parentKey) {
        this.template = template;
        this.parentKey = parentKey;
    }

    public static JedisCacheBuilder newBuilder(RedisTemplate template,String parentKey){
        return new JedisCacheBuilder(template,parentKey);
    }

    public JedisCacheBuilder registerJsonConverter(RedisJsonConverter redisJsonConverter){
        this.redisJsonConverter = redisJsonConverter;
        return this;
    }

    public JedisCacheBuilder expireAfterWrite(long var1,TimeUnit var2){
        this.expireTime = new ExpireTime(var1,var2);
        return this;
    }

    public <K,V> LoadingCache<K,V> build(CacheLoader<K,V> cacheLoader){
        return new JedisLoadingCache(cacheLoader,this);
    }

    public LoadingCache build(){
        return new JedisLoadingCache(this);
    }


    public RedisTemplate getTemplate() {
        return template;
    }

    public ExpireTime getExpireTime() {
        return expireTime;
    }

    public String getParentKey() {
        return parentKey;
    }

    public RedisJsonConverter getRedisJsonConverter() {
        return redisJsonConverter;
    }
}
