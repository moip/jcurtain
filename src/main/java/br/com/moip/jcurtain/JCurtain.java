package br.com.moip.jcurtain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Random;
import java.util.Set;

public class JCurtain {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCurtain.class);

    private JedisPool jedisPool;

    public JCurtain(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
            return isOpenForUser(feature, user) || isFeatureOpen(feature);
        } catch (JedisConnectionException e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
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

    private boolean comparePercentages(int featurePercentage) {
        return randomPercentage() <= featurePercentage;
    }
    
}