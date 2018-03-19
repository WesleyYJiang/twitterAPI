package edu.northeastern.ds4300.twitter;

import java.util.List;
import java.util.Set;

public interface TwitterAPI {
    /**
     * Posts a tweet into the redis database, the tweet can broadcast to time lines if broadcast is
     * passed in as true.
     *
     * @param t Represents the tweet to be posted.
     * @param broadcast  Represents if the post will broadcast to time lines.
     */
    void postTweet(Tweet t, boolean broadcast);

    /**
     * Updates the database with a user following another user.
     *
     * @param userID Represents the user ID of the user being followed.
     * @param followerID  Represents the user ID of the user following the other user.
     */
    void addFollower(String userID, String followerID);

    /**
     * Retrieves the timeline of a user given the user ID.
     *
     * @param userID Represents the user ID of the user of the time line being retrieved.
     * @return a list of tweets, representing the time line of the user.
     */
    List<Tweet> getTimeline(long userID);

    /**
     * Retrieves a list of followers the user follows.
     *
     * @param user_id Represents the user ID of the user.
     * @return a list of followers that follows the given user.
     */
    List<Long> getFollowers(long user_id);

    /**
     * Retrieves a list of followees the user follows.
     *
     * @param user_id Represents the user ID of the user.
     * @return a list of users that the given user follows.
     */
    List<Long> getFollowees(long user_id);

    /**
     * Retrieves a list of tweets the user tweeted.
     *
     * @param user_id Represents the user ID of the user.
     * @return a list of tweets
     */
    List<Tweet> getTweets(long user_id);

    /**
     * Resets the database.
     */
    void reset();
}
