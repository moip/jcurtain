# JCurtain

Easy way to control your features using [redis](http://redis.io/).

Also available for ruby -> [RCurtain](https://github.com/moip/rcurtain)!

## Installation

Maven dependency:

Add this to your POM file

```XML
<dependency>
  <groupId>br.com.moip</groupId>
  <artifactId>jcurtain</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Usage

* JCurtain uses redis to control features, which can be checked by a **percentage** or a **set of users**.
```
feature:[name-of-feature]:percentage
```
```
feature:[name-of-feature]:users
```

* To use JCurtain, first your need to initialize the configuration by defining your **redis URL** (password@ip:port/database).
```java
JCurtain jCurtain = new JCurtain(new URI("redis://:p4ssw0rd@10.0.1.1:6380/15")); 
```

* Check if the curtain is opened for a feature using the method "isOpen", passing the name of the feature you want to check.
```java
jCurtain.isOpen("name-of-the-feature");
```

* You can also pass users to check if they'll see the new feature.
```java
jCurtain.isOpen("name-of-the-feature", "user1");
```

If a user is on the Redis set in **"feature:[name-of-the-feature]:users"** the method will return **true** even when **"feature:[name-of-the-feature]:percentage"** is set to 0 or nil.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/moip/jcurtain. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

1. Fork it (https://github.com/moip/jcurtain/fork)
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create a new Pull Request

## License

This lib is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
