package br.com.moip.jcurtain;

import java.util.Set;

public class Feature {
    private final String name;
    private final int percentage;
    private final Set<String> users;

    public Feature(String name, int percentage, Set<String> users) {
        this.name = name;
        this.percentage = percentage;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public int getPercentage() {
        return percentage;
    }

    public Set<String> getUsers() {
        return users;
    }
}