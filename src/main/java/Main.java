import com.touraj.wordcounter.operation.StatisticsPrinter;
import com.touraj.wordcounter.operation.WordCounter;
import com.touraj.wordcounter.common.FileNumber;
import com.touraj.wordcounter.common.WordCountEntity;
import com.touraj.wordcounter.config.Configuration;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by toraj on 07/03/2018.
 */
public class Main {

    private static final int NUMOFFILEREADERS = 2;

    public static void main(String[] args) {
        Configuration.showWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        String firstFile = "";
        String secondFile = "";

        boolean isFirstFileFound = false;
        while (true) {

            if (!isFirstFileFound) {
                System.out.print("Enter path to First File : ");
                firstFile = scanner.nextLine();

                File file1 = new File(firstFile);
                if (file1.exists() && !file1.isDirectory()) {
                    isFirstFileFound = true;
                } else {
                    System.out.println("First File Not Existing in the path; Try Again!\n");
                    continue;
                }
            }

            System.out.print("Enter path to Second File : ");
            secondFile = scanner.nextLine();

            File file2 = new File(secondFile);
            if (file2.exists() && !file2.isDirectory()) {
                if (secondFile.equalsIgnoreCase(firstFile)) {
                    System.out.println("Second File Should not be same as First File");
                    System.out.println("Please enter another file.");
                } else break;
            } else {
                System.out.println("Second File Not Existing in the path; Try Again!\n");
            }
        }

        Map<String, WordCountEntity> cache = new ConcurrentHashMap();

        //Touraj :: I am using ReentrantLock because the performance of it is better
        // Than Syncronized methods or blocks and also having more control over the part
        // of the code I wanted to lock and better granularity can be also extended better in future
        // to lock in one method and unlock it in another method.
        Lock lock = new ReentrantLock();

        //Touraj :: I use countDownLatch in order to synchronize the Threads on the shared cached
        // So when the both WordCounter threads finished processing file and couting words then
        // the Printer Thread will print out the statistics to the Console
        CountDownLatch countDownLatch = new CountDownLatch(NUMOFFILEREADERS);

        Thread wordCounter1 = new Thread(new WordCounter(firstFile, cache, FileNumber.FIRSTFILE, lock, countDownLatch), "WCThread-1");
        wordCounter1.start();

        Thread wordCounter2 = new Thread(new WordCounter(secondFile, cache, FileNumber.SECONDFILE, lock, countDownLatch), "WCThread-2");
        wordCounter2.start();

        Thread statisticsPrinter = new Thread(StatisticsPrinter.getInstance(countDownLatch, cache), "SP-Thread");
        statisticsPrinter.start();
    }
}
