package br.com.moip.jcurtain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JCurtainTest {

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    private Set<String> testSet;

    private JCurtain jCurtain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        jCurtain = new JCurtain(jedisPool);
        testSet = null;

        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    }

    @Test
    public void returnsTrueOnOneHundredPercent() {
        Mockito.when(jedis.get("feature:feature1:percentage")).thenReturn("100");
        Mockito.when(jedis.smembers("feature:feature1:users")).thenReturn(testSet);

        assertTrue(jCurtain.isOpen("feature1"));
    }

    @Test
    public void returnsFalseOnZeroPercent() {
        Mockito.when(jedis.get("feature:feature2:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature2:users")).thenReturn(testSet);

        assertFalse(jCurtain.isOpen("feature2"));
    }

    @Test
    public void returnsTrueOnListedUser() throws Exception {
        JCurtain jcurtain = new JCurtain(jedisPool);

        Set<String> testSet = new HashSet<String>(Arrays.asList("test-user"));

        Mockito.when(jedis.get("feature:feature3:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature3:users")).thenReturn(testSet);

        assertTrue(jcurtain.isOpen("feature3", "test-user"));
    }


    @Test
    public void returnsFalseOnUnlistedUser() {
        Set<String> testSet = new HashSet<String>(Arrays.asList("test-user"));

        Mockito.when(jedis.get("feature:feature4:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature4:users")).thenReturn(testSet);

        assertFalse(jCurtain.isOpen("feature4", "test-invalid-user"));
    }


    @Test
    public void returnsFalseOnNullPercentage() {
        Mockito.when(jedis.get("feature:feature4:percentage")).thenReturn(null);

        assertFalse(jCurtain.isOpen("feature4"));

    }

    @Test
    public void returnsFalseOnNullUsers() {
        Mockito.when(jedis.get("feature:feature4:users")).thenReturn(null);

        assertFalse(jCurtain.isOpen("feature4", "user-teste"));
    }


    @Test
    public void returnsFalseOnDroppedConnection() {
        Mockito.when(jedis.get(Mockito.anyString())).thenThrow(JedisConnectionException.class);

        assertFalse(jCurtain.isOpen("feature4"));
    }


    @Test
    public void returnsFalseOnFailedConnection() {
        assertFalse(jCurtain.isOpen("feature4"));
    }

}