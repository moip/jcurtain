package br.com.moip.jcurtain;

import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;

public class JCurtain {
    private Jedis jedis;

    public JCurtain(String redis) {
        this.jedis = new Jedis(redis);
    }

    public boolean isOpen(String feature) {
        Feature feat = getFeature(feature);

        return randomPercentage() <= feat.getPercentage();
    }

    public boolean isOpen(String feature, String user) {
        Feature feat = getFeature(feature);

        return feat.getUsers().contains(user) || randomPercentage() <= feat.getPercentage();
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    private Feature getFeature(String name) {
        int percentage = Integer.parseInt(jedis.get("feature:" + name + ":percentage"));
        Set<String> users = jedis.smembers("feature:" + name + ":users");
        return new Feature(name, percentage, users);
    }

    private int randomPercentage() {
        Random random = new Random();
        return random.nextInt(101);
    }
}
