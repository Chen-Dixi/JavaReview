package dixi.bupt.huawei;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    public static void main(String[] args) {
        String s = "  i am  verry  happy ";
        String ans = reverseString(s);
        System.out.println(ans);
    }

    public static String reverseString(String str) {
        String[] res = str.split(" ");

        List<String> resList = new ArrayList<>();
        for (String s : res) {
            if (!"".equals(s)) {
                resList.add(s);
            }
        }
        // 已提取单词
        StringBuilder sb = new StringBuilder();
        for (int i = resList.size() - 1; i > 0; i--) {
            String word = resList.get(i);
            sb.append(word);
            sb.append(" ");
        }
        sb.append(resList.get(0));

        String ans = sb.toString();

        return ans;

    }

    // 反转 char[] [startIndex...endIndex]
//    public static void reverseWord(char[] chars, int startIndex, int endIndex){
//        int left = startIndex, right = endIndex;
//        while(left < right){
//            char tmp = chars[left];
//            chars[left] = chars[right];
//            chars[right] = tmp;
//            left++;
//            right--;
//        }
//    }


}
