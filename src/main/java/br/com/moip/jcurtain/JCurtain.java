package br.com.moip.jcurtain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.URI;
import java.util.Random;
import java.util.Set;

public class JCurtain {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCurtain.class);

    private JedisPool jedisPool;

    public JCurtain(String redis) {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), redis);
    }

    public JCurtain(URI uri) {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), uri);
    }

    public boolean isOpen(String feature) {
        try {
            Feature feat = getFeature(feature);
            return comparePercentages(feat.getPercentage());
        } catch (JedisConnectionException e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    public boolean isOpen(String feature, String user) {
        try {
            Feature feat = getFeature(feature);
            return feat.getUsers().contains(user) || comparePercentages(feat.getPercentage());
        } catch (JedisConnectionException e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    void setJedisPool(JedisPool jedis) {
        this.jedisPool = jedis;
    }

    private Feature getFeature(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            String featurePercentage = jedis.get("feature:" + name + ":percentage");
            if (featurePercentage == null) featurePercentage = "0";
            int percentage = Integer.parseInt(featurePercentage);

            Set<String> users = jedis.smembers("feature:" + name + ":users");
            return new Feature(name, percentage, users);
        }
    }

    private int randomPercentage() {
        Random random = new Random();

        return random.nextInt(100) + 1;
    }

    private boolean comparePercentages(int featurePercentage) {
        return randomPercentage() <= featurePercentage;
    }
}