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


    public Jedis jedis;

    public JCurtain(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isOpen(String feature) {
        try {
            Feature feat = getFeature(feature);
            return comparePercentages(feat.getPercentage());
        } catch (Exception e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    public boolean isOpen(String feature, String user) {
        try {
            Feature feat = getFeature(feature);
            return feat.getUsers().contains(user) || comparePercentages(feat.getPercentage());
        } catch (Exception e) {
            LOGGER.error("[JCurtain] Redis connection failure! Returning default value FALSE. Feature={}", feature);
            return false;
        }
    }

    private Feature getFeature(String name) {
        String featurePercentage = jedis.get("feature:" + name + ":percentage");
        if (featurePercentage == null) featurePercentage = "0";
        int percentage = Integer.parseInt(featurePercentage);

        Set<String> users = jedis.smembers("feature:" + name + ":users");
        return new Feature(name, percentage, users);
    }

    private int randomPercentage() {
        Random random = new Random();

        return random.nextInt(100) + 1;
    }

    private boolean comparePercentages(int featurePercentage) {
        return randomPercentage() <= featurePercentage;
    }
}