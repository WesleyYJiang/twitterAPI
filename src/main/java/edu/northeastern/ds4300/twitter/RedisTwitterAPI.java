package edu.northeastern.ds4300.twitter;

import java.util.List;
import java.util.Date;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import redis.clients.jedis.*;

public class RedisTwitterAPI implements TwitterAPI {
    Jedis jedis = new Jedis("localhost");

    @Override
    public void reset() {
        jedis.flushAll();
    }

    @Override
    public void postTweet(Tweet t, boolean broadcast)
    {
        String key = "tweet:" + t.getUserID() + ":" + getNextID();
        String value = t.toString();
        jedis.set(key,value);
        if (broadcast) { this.broadCast(t); }
    }

    @Override
    public void addFollower(String userID, String followerID)
    {
        String followerKey = "followers:" + userID;
        String followeeKey = "followees:" + followerID;
        jedis.sadd(followerKey, followerID);
        jedis.sadd(followeeKey, userID);
    }

    @Override
    public List<Tweet> getTimeline(long userID)
    {
        String key = "timeline:" + Long.toString(userID);

        if (jedis.exists(key)) {
            return this.retrieveTimeline(key);
        }
        else {
            return this.createTimeline(userID);
        }
    }

    @Override
    public List<Long> getFollowers(long user_id) {
        return getListofUsers(user_id, "followers:");
    }

    @Override
    public List<Long> getFollowees(long user_id) {
        return getListofUsers(user_id, "followees:");
    }

    @Override
    public List<Tweet> getTweets(long user_id) {
        List<Tweet> result = new ArrayList<Tweet>();
        String key = "tweet:" + Long.toString(user_id);
        Set<String> keys = jedis.keys("*" + key + "*");
        for (String s: keys) {
            Tweet t = this.stringToTweet(jedis.get(s));
            result.add(t);
        }
        return result;
    }


    private long getNextID()
    {
        return jedis.incr("nextTweetID");
    }

    private void broadCast(Tweet t) {
        Set<String> followers = jedis.smembers("followers:" + t.getUserID());
        for (String f : followers)
            addToTimeline(t, f);
    }


    private void addToTimeline(Tweet t, String userID)
    {
        String key = "timeline:" + userID;
        String value = t.toString();
        jedis.lpush(key, value);
    }

    private List<Tweet> retrieveTimeline(String key) {
        List<Tweet> result = new ArrayList<>();
        List<String> tweets = jedis.lrange(key, 0, 99);
        for (String s : tweets)
            result.add(this.stringToTweet(s));
        return result;
    }

    private List<Tweet> createTimeline(Long userID) {
        List<Long> followees = this.getFollowees(userID);
        List<Tweet> result = new ArrayList<>();
        for (Long l : followees) {
            result.addAll(this.getTweets(l));
        }
        Collections.sort(result, (s1, s2) -> s1.getCreationDate().compareTo(s2.getCreationDate()));
        return result;
    }

    // CONVERT TWEET STRING TO TWEET
    private Tweet stringToTweet(String s) {
        String[] info = s.split(":");
        Date time = new Date(Long.parseLong(info[0]));
        String userID = info[1];
        String tweet = info[2];

        return new Tweet(userID, time, tweet);
    }

    private List<Long> getListofUsers(long user_id, String type) {
        List<Long> result = new ArrayList<>();
        String key = type + Long.toString(user_id);
        Set<String> l = jedis.smembers(key);
        for (String s : l) {
            result.add(Long.parseLong(s));
        }
        return result;
    }
}
