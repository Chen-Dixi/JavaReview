package dixi.bupt.javaContainer;

import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;

public class LearnHashMap {
    private HashMap<String, Integer> hashMap;
    private Hashtable<String, Integer> hashtable;

    public static void main(String[] args){

        HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.put("123123", true);
        Long id = (long) 123123;

        Boolean res = hashMap.get(StringUtils.join(id));
        System.out.println(res);
    }
}
