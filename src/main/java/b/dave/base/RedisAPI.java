package b.dave.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;
import java.util.function.Function;

public class RedisAPI {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private final JedisPool subscribePool;
    private final JedisPool jedisPool;

    public RedisAPI() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(50);
        config.setMaxTotal(50);
        subscribePool = new JedisPool(config, "Host", 6379, 0, "Password");
        jedisPool = new JedisPool(new JedisPoolConfig(), "Host", 6379, 0, "Password");

    }

    public <T> void publish(RedisChannel redisChannel, T object) {

        if(object.getClass() != redisChannel.getClassType()) {
            throw new RuntimeException("This object can't be sent through this channel.");
        }

        runTransaction(jedis -> jedis.publish(redisChannel.toString().toLowerCase(), GSON.toJson(object)));

    }

    public <T> void subscribe(RedisChannel redisChannel, JedisHandler<T> handler, Class<T> classType) {

        new Thread(() -> runSubscribeTransaction(jedis -> jedis.subscribe(new JedisPubSub() {

            @Override
            public void onMessage(String channel, String message) {
                handler.handle(GSON.fromJson(message, classType));
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                jedis.subscribe(this, redisChannel.toString().toLowerCase());
            }

        }, redisChannel.toString().toLowerCase()))).start();

    }

    public <T> T runReturnTransaction(Function<Jedis, T> action) {

        try(Jedis jedis = jedisPool.getResource()) {
            return action.apply(jedis);
        }

    }

    public void runTransaction(Consumer<Jedis> action) {

        try(Jedis jedis = jedisPool.getResource()) {
            action.accept(jedis);
        }

    }

    public void runSubscribeTransaction(Consumer<Jedis> action) {

        try(Jedis jedis = subscribePool.getResource()) {
            action.accept(jedis);
        }

    }

}
