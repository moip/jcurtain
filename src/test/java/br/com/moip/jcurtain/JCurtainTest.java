package br.com.moip.jcurtain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JCurtainTest {

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    private JCurtain jCurtain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        jCurtain = new JCurtain(jedisPool);

        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    }

    @Test
    public void returnsTrueOnOneHundredPercent() {
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn("100");

        assertTrue(jCurtain.isOpen("name"));
    }

    @Test
    public void returnsFalseOnZeroPercent() {
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn("0");

        assertFalse(jCurtain.isOpen("name"));
    }

    @Test
    public void returnsTrueOnListedUser() throws Exception {
        JCurtain jcurtain = new JCurtain(jedisPool);
        Mockito.when(jedis.sismember("feature:name:users", "test-user")).thenReturn(true);

        assertTrue(jcurtain.isOpen("name", "test-user"));
    }

    @Test
    public void returnTrueOnUnlistedUserButOneHundredPercent() {
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn("100");
        Mockito.when(jedis.sismember("feature:name:users", "test-user")).thenReturn(false);

        assertTrue(jCurtain.isOpen("name", "test-user"));
    }

    @Test
    public void returnsFalseOnUnlistedUser() {
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn("0");
        Mockito.when(jedis.sismember("feature:name:users", "test-user")).thenReturn(true);

        assertFalse(jCurtain.isOpen("name", "test-invalid-user"));
    }

    @Test
    public void returnsFalseOnNullPercentage() {
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn(null);

        assertFalse(jCurtain.isOpen("name"));
    }

    @Test
    public void returnsFalseOnDroppedConnection() {
        Mockito.when(jedis.get(Mockito.anyString())).thenThrow(JedisConnectionException.class);

        assertFalse(jCurtain.isOpen("name"));
    }

    @Test
    public void returnsFalseOnFailedConnection() {
        assertFalse(jCurtain.isOpen("name"));
    }

    @Test
    public void shouldReturnFeature() {
        Set<String> members = Sets.newSet("u1", "u2");
        Mockito.when(jedis.get("feature:name:percentage")).thenReturn("50");
        Mockito.when(jedis.smembers("feature:name:users")).thenReturn(members);

        assertTrue(jCurtain.getFeature("name").equals(new Feature("name", 50, members)));
    }

    @Test
    public void returnUserShouldStoreTrue() {
        Mockito.when(jedisPool.getResource().sismember("feature:name:users", "test-user")).thenReturn(false);
        Mockito.when(jedisPool.getResource().get("feature:name:percentage")).thenReturn("100");
        Mockito.when(jedisPool.getResource().get("feature:name:shouldStoreUser")).thenReturn("true");
        assertTrue(jCurtain.isOpen("name","test-user"));
        Mockito.when(jedisPool.getResource().sismember("feature:name:users", "test-user")).thenReturn(true);
        Mockito.when(jedisPool.getResource().get("feature:name:percentage")).thenReturn("0");
        assertTrue(jCurtain.isOpen("name", "test-user"));
    }

    @Test
    public void returnUserShouldStoreFalse() {
        Mockito.when(jedisPool.getResource().sismember("feature:name:users", "test-user")).thenReturn(false);
        Mockito.when(jedisPool.getResource().get("feature:name:percentage")).thenReturn("100");
        Mockito.when(jedisPool.getResource().get("feature:name:shouldStoreUser")).thenReturn("false");
        assertTrue(jCurtain.isOpen("name","test-user"));
        Mockito.when(jedisPool.getResource().get("feature:name:percentage")).thenReturn("0");
        Mockito.when(jedisPool.getResource().sismember("feature:name:users", "test-user")).thenReturn(false);
        assertFalse(jCurtain.isOpen("name", "test-user"));
    }

}