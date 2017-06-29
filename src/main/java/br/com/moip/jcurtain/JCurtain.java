package br.com.moip.jcurtain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Random;

public class JCurtain {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCurtain.class);

    private JedisPool jedisPool;

    public JCurtain(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean isOpen(String feature) {
        try {
            return isFeatureOpen(feature);
        } catch (JedisConnectionException e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    public boolean isOpen(String feature, String user) {
        try {
            return isOpenForUser(feature, user) || isFeatureOpen(feature);
        } catch (JedisConnectionException e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    public Feature getFeature(String name) {
        Jedis jedis = jedisPool.getResource();
        return new Feature(name, getFeaturePercentage(name), jedis.smembers("feature:" + name + ":users"));
    }

    private boolean isOpenForUser(String feature, String user) {
        return jedisPool.getResource().sismember("feature:" + feature + ":users", user);
    }

    private int getFeaturePercentage(String feature) {
        String featurePercentage = jedisPool.getResource().get("feature:" + feature + ":percentage");
        return (featurePercentage == null ? 0 : Integer.parseInt(featurePercentage));
    }

    private boolean isFeatureOpen(String feature) {
        return randomPercentage() <= getFeaturePercentage(feature);
    }

    private int randomPercentage() {
        return new Random().nextInt(100) + 1;
    }

}