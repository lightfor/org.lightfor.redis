package org.lightfor.redis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * jedis test
 * Created by Light on 2017/7/12.
 */
public class JedisTest {
    @Test
    public void test1() {
        JedisPool pool = init();
        /// Jedis implements Closeable. Hence, the jedis instance will be auto-closed after the last statement.
        try (Jedis jedis = pool.getResource()) {
            /// ... do stuff here ... for example
            jedis.set("foo", "bar");
            String foobar = jedis.get("foo");
            jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike");
            Set<String> sose = jedis.zrange("sose", 0, -1);
        }
        destory(pool);
    }

    private JedisPool init(){
        //You can store the pool somewhere statically, it is thread-safe.
        return new JedisPool(new JedisPoolConfig(), "localhost");
    }

    private void destory(JedisPool pool){
        /// ... when closing your application:
        pool.destroy();
    }

    public void test2(){
        JedisPool pool = init();
        try (Jedis jedis = pool.getResource()) {
            jedis.slaveof("localhost", 6379);  //  if the master is on the same PC which runs your code
            jedis.slaveof("192.168.1.35", 6379);
        }
        destory(pool);
    }

    public void test3(){
        JedisPool pool = init();
        try (Jedis slave1jedis = pool.getResource()) {
            slave1jedis.slaveofNoOne();
            //slave2jedis.slaveof("192.168.1.36", 6379);
        }
        destory(pool);
    }

    public void test4(){
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        //Jedis Cluster will attempt to discover cluster nodes automatically
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 7379));
        JedisCluster jc = new JedisCluster(jedisClusterNodes);
        jc.set("foo", "bar");
        String value = jc.get("foo");
    }


}
