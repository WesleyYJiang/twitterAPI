package edu.northeastern.ds4300.twitter;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class TwitterTester {

    private static TwitterAPI api = new RedisTwitterAPI();

    public static void main(String[] args)
    {
        api.reset();
         //Generating data
        int count = 0;
        int user = 0;
        Tweet[] tweets = new Tweet [1000000];
        for (int i = 0; i < tweets.length; i++) {
            if (count < 99) {
                count++;
            } else {
                count = 0;
                user++;
            }
            tweets[i] = new Tweet(Long.toString(user), new Date(), "SQL is no fun");
        }
        //add followers
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 100; j++) {
                String followerID = Integer.toString(ThreadLocalRandom.current()
                        .nextInt(0, 10000));
                api.addFollower(Integer.toString(i), followerID);
                //System.out.println(i + " followed by " + followerID);
            }
        }
        System.out.println("finished");
         //On average with broadcasting the speed is 4.418 milliseconds per insert
         //Without Broadcasting insertion time is 0.079851 milliseconds per insert
        long start = System.currentTimeMillis();
        for (Tweet tweet : tweets) {
            api.postTweet(tweet, false);
        }
        long end = System.currentTimeMillis();
        System.out.println("Seconds: "+(end-start)/1000.0);

        // On average 38066 milliseconds for retrieving a user's timeline without broadcasting
        // On average 52 milliseconds for retrieving a user's timeline with broadcasting
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            api.getTimeline(i);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Seconds: "+(end2-start2));
    }
}


