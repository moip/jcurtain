package br.com.moip.jcurtain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JCurtainTest {

    @Mock
    private Jedis jedis;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsTrueOnOneHundredPercent()  {
        JCurtain jcurtain = new JCurtain("test1");
        Set<String> testSet = null;
        jcurtain.setJedis(jedis);

        Mockito.when(jedis.get("feature:feature1:percentage")).thenReturn("100");
        Mockito.when(jedis.smembers("feature:feature1:users")).thenReturn(testSet);

        assertTrue(jcurtain.isOpen("feature1"));
    }

    @Test
    public void returnsFalseOnZeroPercent()  {
        JCurtain jcurtain = new JCurtain("test1");
        Set<String> testSet = null;
        jcurtain.setJedis(jedis);

        Mockito.when(jedis.get("feature:feature2:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature2:users")).thenReturn(testSet);

        assertFalse(jcurtain.isOpen("feature2"));
    }

    @Test
    public void returnsTrueOnListedUser() {
        JCurtain jcurtain = new JCurtain("test1");
        jcurtain.setJedis(jedis);

        Set<String> testSet = new HashSet<String>(Arrays.asList("test-user"));

        Mockito.when(jedis.get("feature:feature3:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature3:users")).thenReturn(testSet);

        assertTrue(jcurtain.isOpen("feature3", "test-user"));
    }

    @Test
    public void returnsFalseOnUnlistedUser() {
        JCurtain jcurtain = new JCurtain("test1");
        jcurtain.setJedis(jedis);

        Set<String> testSet = new HashSet<String>(Arrays.asList("test-user"));

        Mockito.when(jedis.get("feature:feature4:percentage")).thenReturn("0");
        Mockito.when(jedis.smembers("feature:feature4:users")).thenReturn(testSet);

        assertFalse(jcurtain.isOpen("feature4", "test-invalid-user"));
    }

}
