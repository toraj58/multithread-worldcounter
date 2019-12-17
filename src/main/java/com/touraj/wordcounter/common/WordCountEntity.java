package com.touraj.wordcounter.common;

/**
 * Created by toraj on 07/03/2018.
 */
public class WordCountEntity implements Comparable<WordCountEntity>{

    int occuranceFileOne;
    int occuranceFileSecond;

    public WordCountEntity(int occuranceFileOne, int occuranceFileSecond) {
        this.occuranceFileOne = occuranceFileOne;
        this.occuranceFileSecond = occuranceFileSecond;
    }

    public int getOccuranceFileOne() {
        return occuranceFileOne;
    }

    public void setOccuranceFileOne(int occuranceFileOne) {
        this.occuranceFileOne = occuranceFileOne;
    }

    public int getOccuranceFileSecond() {
        return occuranceFileSecond;
    }

    public void setOccuranceFileSecond(int occuranceFileSecond) {
        this.occuranceFileSecond = occuranceFileSecond;
    }

    @Override
    public int compareTo(WordCountEntity wordCountEntity) {
        int total1 = this.getOccuranceFileOne() + this.getOccuranceFileSecond();
        int total2 = wordCountEntity.getOccuranceFileOne() + wordCountEntity.getOccuranceFileSecond();
        return total1 == total2 ? 0 : total1 > total2 ? -1 : 1;
    }
}
