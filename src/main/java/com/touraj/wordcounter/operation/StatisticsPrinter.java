package com.touraj.wordcounter.operation;

import com.touraj.wordcounter.common.WordCountEntity;
import com.touraj.wordcounter.config.Configuration;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by toraj on 07/03/2018.
 */
public class StatisticsPrinter implements Runnable {

    private CountDownLatch countDownLatch;
    private Map<String, WordCountEntity> cache;

    private static StatisticsPrinter instance;

    private StatisticsPrinter(){}

    private StatisticsPrinter(CountDownLatch countDownLatch, Map<String, WordCountEntity> cache) {
        this.countDownLatch = countDownLatch;
        this.cache = cache;
    }

    //Touraj : StatisticsPrinter is Singelton
    public static synchronized StatisticsPrinter getInstance(CountDownLatch countDownLatch, Map<String, WordCountEntity> cache){
        if(instance == null){
            instance = new StatisticsPrinter(countDownLatch, cache);
        }
        return instance;
    }

    @Override
    public void run() {

        try {
            //Touraj: Wait for WordCounter Threads to parse the files and populate the Cache
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Configuration.showStatMessage();

        cache.entrySet().stream()
                .sorted(Map.Entry.<String, WordCountEntity>comparingByValue())
                .forEach(k -> System.out.println(String.format(
                        "%s %d = %d + %d",
                        k.getKey(),
                        k.getValue().getOccuranceFileOne() + k.getValue().getOccuranceFileSecond() ,
                        k.getValue().getOccuranceFileOne(), k.getValue().getOccuranceFileSecond())));
    }
}