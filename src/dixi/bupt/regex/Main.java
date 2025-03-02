package dixi.bupt.regex;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class Main {

    public static void main(String[] args) {

        String source = "$businessInfo.distributeLeader";
        String[] sources = trimToEmpty(source).split(",");

        for (String sourceName : sources) {
            System.out.println(sourceName);
        }
    }

    @Test
    public void test() {
        String alertMsg = "服务名称: plateco-kwaishop-industry-center-grpc\n\n环境: PROD\n\n简单异常类名: NullPointerException";
        // == 提取 alertMsg ==
        // 使用正则表达式分割字符串
        String[] parts = alertMsg.split("\\n\\n");

        // 提取服务名称和异常类名
        String serviceName = parts[0].split(": ")[1];
        String environment = parts[1].split(": ")[1];
        String simpleExceptionName = parts[2].split(": ")[1];

        System.out.println("服务名称: " + serviceName);
        System.out.println("环境: " + environment);
        System.out.println("简单异常类名: " + simpleExceptionName);
    }

    public static String extractValueByRegex(String input, String key) {
        String regex = key + ": (.+?)( |$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}
