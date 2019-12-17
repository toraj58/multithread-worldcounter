package com.touraj.wordcounter.config;

/**
 * Created by toraj on 07/03/2018.
 */
public class Configuration {

    private static final String welcomeMessage = "" +
            "**********************************\n" +
            "**                              **\n" +
            "**     File Word Counter        **\n" +
            "**        Developed By          **\n" +
            "**       Touraj Ebrahimi        **\n" +
            "**                              **\n" +
            "**********************************\n";

    private static final String statisticsMessage = "" +
            "**********************************\n" +
            "* Statistics of words occurrence *\n" +
            "**********************************\n";

    public static String getWelcomeMessage() {
        return welcomeMessage;
    }

    public static String getStatisticsMessage() {
        return statisticsMessage;
    }

    public static void showWelcomeMessage() {
        System.out.println(Configuration.getWelcomeMessage());
    }

    public static void showStatMessage() {
        System.out.println(Configuration.getStatisticsMessage());
    }
}
