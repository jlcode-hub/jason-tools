package com.jason.liu.time.utils;


import com.jason.liu.time.consume.MethodInfo;
import com.jason.liu.time.consume.TimeConsume;
import com.jason.liu.time.statistics.StatisticSummary;

import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
public class TimeLogUtils {

    public static MethodInfo buildMethodInfo(Method method, Class<?> clazz, TimeConsume timeConsume) {
        String className = NameUtils.className(clazz);
        String methodName = NameUtils.methodName(method);
        int cLen = Math.max(5, className.length() + 5);
        int mLen = Math.max(6, methodName.length() + 5);
        StringBuilder titleBuild = new StringBuilder();
        titleBuild.append(String.format("%" + cLen + "s", "Class")).append("|");
        titleBuild.append(String.format("%" + mLen + "s", "Method")).append("|");
        titleBuild.append(String.format("%10s", "Exception")).append("|");
        titleBuild.append(String.format("%20s", "Time(ms)"));
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setKey(NameUtils.key(timeConsume, className, methodName));
        methodInfo.setClassName(className);
        methodInfo.setClassLen(cLen);
        methodInfo.setMethodName(methodName);
        methodInfo.setMethodLen(mLen);
        methodInfo.setTimeConsume(timeConsume);
        methodInfo.setTitle(titleBuild.toString());
        return methodInfo;
    }

    public static void printInfo(StringBuilder builder, MethodInfo methodInfo, boolean exception, long times) {
        builder.append(String.format("%" + methodInfo.getClassLen() + "s", methodInfo.getClassName())).append("|");
        builder.append(String.format("%" + methodInfo.getMethodLen() + "s", methodInfo.getMethodName())).append("|");
        builder.append(String.format("%10s", exception ? "YES" : "NO")).append("|");
        builder.append(String.format("%20s", times));
        printNewLine(builder);
    }


    public static void printNewLine(StringBuilder builder) {
        builder.append("\n");
    }

    public static void printTitle(StringBuilder builder, String title) {
        builder.append(title);
        printNewLine(builder);
        printSepLine(builder, title);
    }

    public static void printSepLine(StringBuilder builder, String title) {
        title.chars().forEach((c) -> {
            if (c == '|') {
                builder.append('+');
            } else {
                builder.append('-');
            }
        });
        printNewLine(builder);
    }

    public static String statisticTitle(int maxClassLen, int maxMethodLen) {
        StringBuilder titleBuild = new StringBuilder();
        titleBuild.append(String.format("%" + maxClassLen + "s", "Class")).append("|");
        titleBuild.append(String.format("%" + maxMethodLen + "s", "Method")).append("|");
        titleBuild.append(String.format("%10s", "Total")).append("|");
        titleBuild.append(String.format("%10s", "Min(ms)")).append("|");
        titleBuild.append(String.format("%10s", "Max(ms)")).append("|");
        titleBuild.append(String.format("%10s", "Avg(ms)")).append("|");
        titleBuild.append(String.format("%10s", "Success")).append("|");
        titleBuild.append(String.format("%10s", "Exception")).append("|");
        titleBuild.append(String.format("%10s", "Success Rate"));
        return titleBuild.toString();
    }

    public static void printStatisticInfo(StringBuilder builder, int maxClassLen, int maxMethodLen, MethodInfo methodInfo, StatisticSummary summary) {
        builder.append(String.format("%" + maxClassLen + "s", methodInfo.getClassName())).append("|");
        builder.append(String.format("%" + maxMethodLen + "s", methodInfo.getMethodName())).append("|");
        builder.append(String.format("%10d", summary.getCalledTimes())).append("|");
        builder.append(String.format("%10d", summary.getMinTimeConsume())).append("|");
        builder.append(String.format("%10d", summary.getMaxTimeConsume())).append("|");
        builder.append(String.format("%10d", summary.getTotalTimeConsume() / summary.getCalledTimes())).append("|");
        builder.append(String.format("%10d", summary.getSuccessTimes())).append("|");
        builder.append(String.format("%10d", summary.getCalledTimes() - summary.getSuccessTimes())).append("|");
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(2);
        builder.append(String.format("%10s", numberFormat.format((float) summary.getSuccessTimes() / (float) summary.getCalledTimes()))).append("|");
        printNewLine(builder);
    }

    private static String EMPTY_INFO;

    static {
        StringBuilder emptyBuilder = new StringBuilder();
        String empty = String.format("%10s", "0");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(empty).append("|");
        emptyBuilder.append(String.format("%10s", "0.00%")).append("|");
        EMPTY_INFO = emptyBuilder.toString();
    }

    public static void printEmptyStatisticInfo(StringBuilder builder, int maxClassLen, int maxMethodLen, MethodInfo methodInfo) {
        builder.append(String.format("%" + maxClassLen + "s", methodInfo.getClassName())).append("|");
        builder.append(String.format("%" + maxMethodLen + "s", methodInfo.getMethodName())).append("|");
        builder.append(EMPTY_INFO);
        printNewLine(builder);
    }
}
