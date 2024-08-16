package dixi.bupt.regex;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Main {

    public static void main(String[] args) {
        // write your code here
//        String DOMAIN_REGEX = "^(?:(https?:\\/\\/)?)?[a-z0-9][a-z0-9-]{0,62}(?:\\.[a-z0-9][a-z0-9-]{0,62})+(?::"
//                + "([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))?$";
//        Pattern pattern = Pattern.compile(DOMAIN_REGEX);
//        List<String> matches = new ArrayList<>(Arrays.asList("www.google.com", "https://www.google.com", "google.com", "baidu.com:8080"));
//        for (String match : matches) {
//            Matcher matcher = pattern.matcher(match);
//            if (!matcher.find()) {
//                System.out.println(match);
//            }
//        }

        String source = "$businessInfo.distributeLeader";
        String[] sources = trimToEmpty(source).split(",");

        for (String sourceName : sources) {
            System.out.println(sourceName);
        }
    }
}
