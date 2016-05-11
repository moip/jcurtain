package br.com.moip.jcurtain;

import java.util.Set;

public class Feature {
    String name;
    int percentage;
    Set<String> users;

    public Feature(String name, int percentage, Set<String> users) {
        this.name = name;
        this.percentage = percentage;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
