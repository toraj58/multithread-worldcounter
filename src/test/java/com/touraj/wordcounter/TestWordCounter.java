package com.touraj.wordcounter;

import com.touraj.wordcounter.common.FileNumber;
import com.touraj.wordcounter.common.WordCountEntity;
import com.touraj.wordcounter.operation.WordCounter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by toraj on 07/03/2018.
 */
public class TestWordCounter {

    private static final String FIRSTFILETEST = "tourajtest1.txt";
    private static final String SECONDFILETEST = "tourajtest2.txt";
    private static final int NUMOFFILEREADERS = 2;

    @Test
    public void testWordCount() {

        System.out.println("Creating Test Files");

        List<String> lines1 = new ArrayList<>();
        lines1.add("in1 in2 in3");
        lines1.add("in4 in5 in6");
        lines1.add("in7 in8 in9");
        lines1.add("in1 in1 in2");

        List<String> lines2 = new ArrayList<>();
        lines2.add("on1 on2 on3");
        lines2.add("on4 on5 on6");
        lines2.add("on7 on8 on9");
        lines2.add("on9 on9 on9");

        try {
            Files.write(Paths.get(FIRSTFILETEST), lines1);
            Files.write(Paths.get(SECONDFILETEST), lines2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Test Files Created...Ready for word count!");

        Map<String, WordCountEntity> cache = new ConcurrentHashMap();
        Lock lock = new ReentrantLock();
        CountDownLatch countDownLatch = new CountDownLatch(NUMOFFILEREADERS);

        Thread wordCounter1 = new Thread(new WordCounter(FIRSTFILETEST, cache, FileNumber.FIRSTFILE, lock, countDownLatch), "TestThreadWC-1");
        wordCounter1.start();

        Thread wordCounter2 = new Thread(new WordCounter(SECONDFILETEST, cache, FileNumber.SECONDFILE, lock, countDownLatch), "TestThreadWC-2");
        wordCounter2.start();

        try {
            //Touraj :: Waiting for WordCounter Threads to be finished!
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isTestPassed = false;
        WordCountEntity wordCountEntity = cache.get("on9");

        if (wordCountEntity.getOccuranceFileSecond() == 4) {
            isTestPassed = true;
        }

        WordCountEntity wordCountEntity2 = cache.get("in1");
        if (wordCountEntity2.getOccuranceFileOne() != 3) {
            isTestPassed = false;
        }

        WordCountEntity wordCountEntity3 = cache.get("in6");
        if (wordCountEntity3.getOccuranceFileOne() != 1) {
            isTestPassed = false;
        }

        try {
            Files.delete(Paths.get(FIRSTFILETEST));
            Files.delete(Paths.get(SECONDFILETEST));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(isTestPassed);
    }

    @Test
    public void testSortHashMapByValueWordCountEntity() {

        WordCountEntity wordCountEntity1 = new WordCountEntity(1, 1);
        WordCountEntity wordCountEntity2 = new WordCountEntity(3, 1);
        WordCountEntity wordCountEntity3 = new WordCountEntity(10, 2);
        WordCountEntity wordCountEntity4 = new WordCountEntity(11, 11);

        Map<String, WordCountEntity> cache = new ConcurrentHashMap();

        cache.put("word1", wordCountEntity1);
        cache.put("word2", wordCountEntity2);
        cache.put("word3", wordCountEntity3);
        cache.put("word4", wordCountEntity4);

        Map<String, WordCountEntity> result = cache.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        StringBuilder accumulator = new StringBuilder();
        result.forEach((k, v) -> accumulator.append(k));

        String afterSort = "word4word3word2word1";

        Assert.assertEquals(afterSort, accumulator.toString());
    }
}