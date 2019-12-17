package com.touraj.wordcounter.operation;

import com.touraj.wordcounter.common.FileNumber;
import com.touraj.wordcounter.common.WordCountEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

/**
 * Created by toraj on 07/03/2018.
 */
public class WordCounter implements Runnable{

    private String fileName;
    private Map<String, WordCountEntity> cache;
    private FileNumber fileNumber;
    private Lock lock;
    private CountDownLatch countDownLatch;
    private static final int MAXNUMWORDS = 500;

    public WordCounter(String fileName,
                       Map<String, WordCountEntity> cache,
                       FileNumber fileNumber,
                       Lock lock,
                       CountDownLatch countDownLatch) {
        this.fileName = fileName;
        this.cache = cache;
        this.fileNumber = fileNumber;
        this.lock = lock;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {

        //Touraj :: Because Stream is AutoClosable I use Try resource block here
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            //Touraj:: Because of the Scope of the Lambda can not use simple int.
            final int[] wordcount = {0};
            stream.forEach(line -> {
                if (wordcount[0] > MAXNUMWORDS) {
                    return;
                }
                for (StringTokenizer stringTokenizer = new StringTokenizer(line); stringTokenizer.hasMoreTokens(); ) {
                    String word = stringTokenizer.nextToken();
                    ++wordcount[0];

                    if (wordcount[0] > MAXNUMWORDS) {
                        break;
                    }

                    //Touraj :: Entering Critical Section of the Code where the Operation is Not Atomic so Using Lock
                    lock.lock();
                    cache.computeIfPresent(word, (k,v) -> {
                        if (fileNumber == FileNumber.FIRSTFILE) {
                            v.setOccuranceFileOne(v.getOccuranceFileOne() + 1);
                            return v;
                        } else
                        {
                            v.setOccuranceFileSecond(v.getOccuranceFileSecond() + 1);
                            return v;
                        }
                    });

                    cache.computeIfAbsent(word, v -> {
                        if (fileNumber == FileNumber.FIRSTFILE) {
                            return new WordCountEntity(1, 0);
                        } else{
                            return new WordCountEntity(0, 1);
                        }
                    });
                    //Toura :: Exiting the Critical Section of the Code so Unlocking
                    lock.unlock();
                }
            });

            //Touraj :: decrease Latch to give the cpu to StatisticsPrinter Thread when it went down to 0
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}