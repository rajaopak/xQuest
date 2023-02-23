package id.rajaopak.xquest.redis;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisManager {

    private final XQuest plugin;
    public ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean auth;
    private String password;
    private JedisPool subscriberPool;
    private JedisPool publisherPool;
    private JedisPubSub pubSub;
    private String channel;

    public RedisManager(XQuest plugin) {
        this.plugin = plugin;
    }

    public boolean connect(String host, int port, @Nullable String password, String channel) {
        this.channel = channel;
        this.password = password;
        this.auth = password != null && password.length() > 0;

        try {
            this.subscriberPool = this.publisherPool = new JedisPool(new JedisPoolConfig(), host, port, 30000, password);
            this.setupSub();
        } catch (Exception e) {
            this.subscriberPool = this.publisherPool = null;
            Utils.logSevere("Failed to connect to the redis server.");
        }

        return isRedisConnected();
    }

    public void sendRequest(JSONObject object) {
        try {
            if (object == null) {
                throw new IllegalStateException("Object that was being sent was null!");
            }

            Jedis jedis = this.publisherPool.getResource();
            Throwable throwable = null;

            try {
                try {
                    if (this.auth) {
                        jedis.auth(this.password);
                    }

                    jedis.publish(this.channel, object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Throwable e) {
                throwable = e;
                throw e;
            } finally {
                if (jedis != null) {
                    if (throwable != null) {
                        try {
                            jedis.close();
                        } catch (Throwable e) {
                            throwable.addSuppressed(e);
                        }
                    } else {
                        jedis.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRedisConnected() {
        if (this.subscriberPool != null && !this.subscriberPool.isClosed() && this.publisherPool != null &&  !this.publisherPool.isClosed()) {
            try (Jedis subsPool = this.subscriberPool.getResource(); Jedis pubPool = this.publisherPool.getResource()) {
                return subsPool.isConnected() && pubPool.isConnected();
            } catch (JedisConnectionException ignore) {
                /*Utils.logSevere("Redis is not connected");*/
            }
        }
        return false;
    }

    public void close() {
        try {
            if (this.pubSub != null && this.pubSub.isSubscribed()) {
                this.pubSub.unsubscribe();
            }

            if (this.subscriberPool != null && !this.subscriberPool.isClosed()) {
                this.subscriberPool.close();
            }

            if (this.publisherPool != null && !this.publisherPool.isClosed()) {
                this.publisherPool.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSub() {
        this.pubSub = new PubSubListener(this.plugin);

        if (this.channel.isEmpty() || this.channel.isBlank()) {
            return;
        }

        this.executor.execute(() -> {
            try {
                Jedis jedis = this.subscriberPool.getResource();
                Throwable throwable = null;

                try {
                    jedis.subscribe(this.pubSub, this.channel);
                } catch (Throwable e) {
                    throwable = e;
                    throw e;
                } finally {
                    if (jedis != null) {
                        if (throwable != null) {
                            try {
                                jedis.close();
                            } catch (Throwable e) {
                                throwable.addSuppressed(e);
                            }
                        } else {
                            jedis.close();
                        }
                    }
                }
            } catch (Exception e) {
                this.subscriberPool = this.publisherPool = null;
                Utils.logSevere("Failed to subscribe the redis channel.");
            }
        });
    }

}

