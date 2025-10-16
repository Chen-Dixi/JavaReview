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

    @Test
    public void testSplitSeparator() {
        String s = "asd";
        s = unescapePattern(s);
        String[] seps = s.split("\\|");
        for (int i = 0; i < seps.length; i++) {
            seps[i] = Pattern.quote(seps[i]);
        }
        for (int i = 0; i < seps.length; i++) {
            System.out.println(seps[i]);
        }

        String separatorPattern = String.join("|", seps);
        String content = "asd\nasdasd\nasdasdasd";
        String[] segments = content.split(separatorPattern);

        for (String segment : segments) {
            System.out.println(segment);
        }
        System.out.println(segments.length);
    }

    /**
     * 处理分隔符的转义并创建分割模式
     * @param separatorPattern 原始分隔符（可能包含转义字符）
     * @return 处理后的分割模式
     */
    public static String createSplitPattern(String separatorPattern) {
        try {
            // 处理常见的转义序列
            String unescaped = unescapePattern(separatorPattern);
            // 创建后行断言模式
            return "(?<=" + unescaped + ")";
        } catch (Exception e) {
            // 发生错误时返回默认的换行符模式
            return "(?<=\n)";
        }
    }

    /**
     * 处理转义字符
     */
    private static String unescapePattern(String pattern) {
        return pattern
                .replaceAll("\\\\n", "\n")   // 处理换行符
                .replaceAll("\\\\t", "\t")   // 处理制表符
                .replaceAll("\\\\r", "\r");  // 处理回车符
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
