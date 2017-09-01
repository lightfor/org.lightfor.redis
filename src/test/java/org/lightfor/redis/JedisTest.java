package org.lightfor.redis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
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
            System.out.println(foobar);
            jedis.zadd("sose", 0, "car");
            jedis.zadd("sose", 0, "bike");
            Set<String> sose = jedis.zrange("sose", 0, -1);
            System.out.println(sose);
        }
        destory(pool);
    }

    private JedisPool init(){
        //You can store the pool somewhere statically, it is thread-safe.
        return new JedisPool(new JedisPoolConfig(), "localhost", 32778);
    }

    private void destory(JedisPool pool){
        /// ... when closing your application:
        pool.destroy();
    }

    public void test2(){
        JedisPool pool = init();
        try (Jedis jedis = pool.getResource()) {
            jedis.slaveof("localhost", 32770);  //  if the master is on the same PC which runs your code
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

    @Test
    public void test5(){
        JedisCluster jedisCluster = getJedisCluster();
        System.out.println(jedisCluster.set("test", "test", "NX", "EX", 10));
        System.out.println(jedisCluster.set("test", "test", "NX", "EX", 10));
        closeJedisCluster(jedisCluster);
    }

    private JedisCluster getJedisCluster() {
        Set<HostAndPort> node = new HashSet<>();
        HostAndPort hostAndPort = new HostAndPort("127.0.0.1", 32770);
        node.add(hostAndPort);
        int timeout = 1000;
        int maxAttempts = 3;
        return new JedisCluster(node, timeout, maxAttempts);
    }

    private void closeJedisCluster(JedisCluster jedisCluster){
        try {
            jedisCluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test6(){
        JedisCluster jedisCluster = getJedisCluster();
        System.out.println(jedisCluster.get("test"));
        closeJedisCluster(jedisCluster);
    }

    @Test
    //60-100
    public void test7(){
        JedisPool jedisPool = init();
        long startTime = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if (i % 250 == 0) {
                    startTime = System.currentTimeMillis();
                }
                jedis.set(""+i, ""+i);
                if (i % 250 == 249) {
                    System.out.println(System.currentTimeMillis() - startTime);
                }
            }
        }
    }

    @Test
    //60-100
    public void test8(){
        JedisPool jedisPool = init();
        long startTime = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if (i % 250 == 0) {
                    startTime = System.currentTimeMillis();
                }
                jedis.get(""+i);
                if (i % 250 == 249) {
                    System.out.println(System.currentTimeMillis() - startTime);
                }
            }
        }
    }


}
