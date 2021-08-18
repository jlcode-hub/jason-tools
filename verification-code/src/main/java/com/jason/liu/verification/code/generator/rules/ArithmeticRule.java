package com.jason.liu.verification.code.generator.rules;

import com.jason.liu.verification.code.ICodeRule;
import com.jason.liu.verification.code.constants.CodeRuleType;
import com.jason.liu.verification.code.model.RandomCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO: 四则运算规则
 */
@Slf4j
public class ArithmeticRule implements ICodeRule {

    private final Random random = new Random();

    private Integer numberRange;

    public ArithmeticRule(Integer numberRange) {
        if (null == numberRange || numberRange < 10) {
            log.warn("number range is less than 10, change to 100");
            this.numberRange = 100;
        } else {
            this.numberRange = numberRange;
        }
    }

    @Override
    public RandomCode createCode() {
        int first = random.nextInt(numberRange);
        Operation operation = Operation.random();
        int second, verifyCode;
        if (operation == Operation.divide) {
            if (0 == first) {
                second = random.nextInt(numberRange) + 1;
            } else {
                second = first;
                if (first > 20) {
                    first = first * (random.nextInt(5) + 1);
                } else {
                    first = first * (random.nextInt(50) + 1);
                }
            }
            verifyCode = first / second;
        } else if (operation == Operation.sub) {
            if (first > 20) {
                second = random.nextInt(30) + 1;
            } else {
                second = random.nextInt(numberRange);
            }
            if (first - second < 0) {
                int tmp = first;
                first = second;
                second = tmp;
            }
            verifyCode = first - second;
        } else if (operation == Operation.multiply) {
            if (0 == first) {
                second = random.nextInt(numberRange) + 1;
            } else {
                if (first > 10) {
                    second = random.nextInt(10) + 1;
                } else {
                    second = random.nextInt(numberRange);
                }
            }
            verifyCode = first * second;
        } else {
            if (first > 20) {
                second = random.nextInt(30) + 1;
            } else {
                second = random.nextInt(numberRange);
            }
            verifyCode = first + second;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Number.randomChar(first))
                .append(operation.flag)
                .append(Number.randomChar(second))
                .append("=?");
        return new RandomCode(String.valueOf(verifyCode), builder.toString());
    }

    @Override
    public CodeRuleType ruleType() {
        return CodeRuleType.ARITHMETIC;
    }


    public enum Operation {
        /**
         * 加
         */
        add("+"),
        /**
         * 减
         */
        sub("-"),
        /**
         * 乘
         */
        multiply("×"),
        /**
         * 除
         */
        divide("÷");

        private String flag;

        Operation(String flag) {
            this.flag = flag;
        }

        private static final Random random = new Random();

        public static Operation random() {
            return Operation.values()[random.nextInt(Operation.values().length)];
        }
    }

    public enum Number {
        /**
         * 数字
         */
        zero(0, "零"),
        one(1, "壹"),
        two(2, "贰"),
        three(3, "叁"),
        four(4, "肆"),
        five(5, "伍"),
        six(6, "陆"),
        seven(7, "柒"),
        eight(8, "捌"),
        nine(9, "玖");

        private int num;

        private String zh;

        Number(int num, String zhNum) {
            this.num = num;
            this.zh = zhNum;
        }

        private static Map<Integer, Number> numberMap = new HashMap<>();

        private static final Random random = new Random();

        static {
            for (Number number : Number.values()) {
                numberMap.put(number.num, number);
            }
        }

        public static Number ofNum(int i) {
            return numberMap.get(i);
        }

        public static String randomChar(int num) {
            Number number = ofNum(num);
            if (null == number) {
                return String.valueOf(num);
            }
            return random.nextInt(10) >= 5 ? number.zh : String.valueOf(num);
        }
    }
}
