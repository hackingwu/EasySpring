package cache;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public interface RedisJsonConverter {
    public Object convert(byte[] value);
}
