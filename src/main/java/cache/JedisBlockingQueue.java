package cache;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 为兼容之前ArrayBlockingQueue的接口，实际没有完全的实现Blocking的所有功能；把底层的Array存储更换成Redis
 * 由于部队redis的list设置大小，因此再写上没有设置阻塞，只对读设置阻塞。
 * redis的list使用FIFO，LEFT IN，RIGHT OUT
 * 只实现了需要的clear，offer，poll，poll(long,TimeUnit)方法
 * @author hackingwu.
 * @since 2015/08/15
 */
public class JedisBlockingQueue<E> {


    RedisTemplate redisTemplate;

    ListOperations listOperations;

    private String cacheKey;

    private RedisJsonConverter redisJsonConverter;

    final ReentrantLock lock;

    private final Condition notEmpty;

    public JedisBlockingQueue(RedisTemplate redisTemplate,String cacheKey,RedisJsonConverter redisJsonConverter){
        this(redisTemplate,cacheKey,redisJsonConverter,false);
    }

    public JedisBlockingQueue(RedisTemplate redisTemplate,String cacheKey,RedisJsonConverter redisJsonConverter,boolean fair) {
        this.redisTemplate = redisTemplate;
        listOperations = redisTemplate.opsForList();
        this.cacheKey = cacheKey;
        this.redisJsonConverter = redisJsonConverter;
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
    }

    public Iterator<E> iterator() {
        return null;
    }

    public long size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return listOperations.size(getKey());
        }finally {
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try{
            while (listOperations.size(getKey()) == 0){
                if (nanos <= 0) return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return extract();
        }finally {
            lock.unlock();
        }
    }


    public int drainTo(Collection<? super E> c) {
        checkNotNull(c);
        if (c == this) throw new IllegalArgumentException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            int n = 0;
            long max = listOperations.size(getKey());
            while (n < max){
                c.add(extract());
                ++n;
            }

            return n;
        }finally {
            lock.unlock();
        }
    }



    public boolean offer(E e) {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            insert(e);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return  extract();
        }finally {
            lock.unlock();
        }
    }


    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            while (true){
                if(poll()==null)break;
            }
        }finally {
            lock.unlock();
        }

    }

    private void insert(E x){
        listOperations.leftPush(getKey(),x);
        notEmpty.signal();
    }

    private E extract(){
        byte[] redisResult = (byte[])listOperations.rightPop(getKey());
        Object value = null;
        if (redisResult != null){
            if (redisJsonConverter != null){
                value = redisJsonConverter.convert(redisResult);
            }else{
                value = JSON.parse(redisResult);
            }
        }
        return cast(value);
    }

    private static <E> E cast(Object item){
        return (E)item;
    }

    private static void checkNotNull(Object e){
        if (e == null) throw new NullPointerException();
    }

    private String getKey(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(cacheKey);
        stringBuffer.append(":");
        return stringBuffer.toString();
    }


}
