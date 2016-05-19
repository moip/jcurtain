package br.com.moip.jcurtain;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.URI;
import java.util.Random;
import java.util.Set;

public class JCurtain {
    private Jedis jedis;

    public JCurtain(String redis) {
        this.jedis = new Jedis(redis);
    }

    public JCurtain(URI uri) {
        this.jedis = new Jedis(uri);
    }

    public boolean isOpen(String feature) {
        try {
            Feature feat = getFeature(feature);
            return redisIsUp() && comparePercentages(feat.getPercentage());
        } catch (JedisConnectionException e) {
            return false;
        }
    }

    public boolean isOpen(String feature, String user) {
        try {
            Feature feat = getFeature(feature);
            return redisIsUp() && (feat.getUsers().contains(user) || comparePercentages(feat.getPercentage()));
        } catch (JedisConnectionException e) {
            return false;
        }
    }

    void setJedis(Jedis jedis) {
        this.jedis = jedis;
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

    private boolean redisIsUp() {
        return jedis != null;
    }

    private boolean comparePercentages(int featurePercentage) {
        return randomPercentage() <= featurePercentage;
    }
}