package br.com.moip.jcurtain;

import java.util.Set;

public class Feature {

    private final String name;
    private final int percentage;
    private final Set<String> users;
    private final boolean isFixedUser;

    public Feature(String name, int percentage, Set<String> users) {
        this(name,percentage,users,false);
    }

    public Feature(String name, int percentage, Set<String> users, boolean isFixedUser) {
        this.name = name;
        this.percentage = percentage;
        this.users = users;
        this.isFixedUser = isFixedUser;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        if (percentage != feature.percentage) return false;
        if (name != null ? !name.equals(feature.name) : feature.name != null) return false;
        return users != null ? users.equals(feature.users) : feature.users == null;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "name='" + name + '\'' +
                ", percentage=" + percentage +
                ", users=" + users +
                ", isFixedUser=" + isFixedUser +
                '}';
    }

    public boolean isFixedUser() {
        return isFixedUser;
    }
}