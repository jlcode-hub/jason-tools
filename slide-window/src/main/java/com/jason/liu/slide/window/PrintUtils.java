package com.jason.liu.slide.window;

import java.util.List;

/**
 * @author: meng.liu
 * @date: 2021/3/23
 * TODO:
 */
public class PrintUtils {

    private static String TITLE;

    private static String LINE;

    private static String EMPTY_INFO;

    static {
        StringBuilder titleBuild = new StringBuilder();
        titleBuild.append(String.format("%20s", "Key")).append("|");
        titleBuild.append(String.format("%15s", "Total")).append("|");
        titleBuild.append(String.format("%15s", "TPS")).append("|");
        TITLE = titleBuild.toString();
        StringBuilder lineBuild = new StringBuilder();
        TITLE.chars().forEach((c) -> {
            if (c == '|') {
                lineBuild.append('+');
            } else {
                lineBuild.append('-');
            }
        });
        LINE = lineBuild.toString();
    }

    public static void printTitle(StringBuilder builder) {
        builder.append(TITLE);
        printNewLine(builder);
        printSepLine(builder);
    }

    public static void printNewLine(StringBuilder builder) {
        builder.append("\n");
    }

    public static void printSepLine(StringBuilder builder) {
        builder.append(LINE);
        printNewLine(builder);
    }

    public static void printStatisticInfo(StringBuilder builder, StatisticSummary summary) {
        builder.append(String.format("%20s", nameFormat(summary.getKey(), 20, "."))).append("|");
        builder.append(String.format("%15d", summary.getTotal())).append("|");
        builder.append(String.format("%15s", summary.getTps())).append("|");
        printNewLine(builder);
        printSepLine(builder);
    }

    public static String nameFormat(String name, int maxLen, String placeholder) {
        if (name.length() <= maxLen || maxLen < 5) {
            return name;
        }
        char[] chars = name.toCharArray();
        int cLen = (maxLen - 3) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cLen; i++) {
            builder.append(chars[i]);
        }
        builder.append(placeholder).append(placeholder).append(placeholder);
        for (int i = chars.length - cLen; i < chars.length; i++) {
            builder.append(chars[i]);
        }
        return builder.toString();
    }

    public static String print(List<StatisticSummary> summaryList) {
        StringBuilder builder = new StringBuilder();
        printTitle(builder);
        for (StatisticSummary summary : summaryList) {
            printStatisticInfo(builder, summary);
        }
        return builder.toString();
    }
}
