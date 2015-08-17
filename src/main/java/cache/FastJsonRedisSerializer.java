package cache;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class FastJsonRedisSerializer implements RedisSerializer {
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return JSONObject.toJSONBytes(o, SerializerFeature.WriteDateUseDateFormat);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return bytes;
    }
}
