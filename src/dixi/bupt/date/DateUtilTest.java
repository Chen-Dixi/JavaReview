package dixi.bupt.date;

import java.text.SimpleDateFormat;

/**
 * Created on 2022-07-30
 */
public class DateUtilTest {
    public static String getDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(timeStamp);
    }

    public static void main(String[] args) {
        long mils = System.currentTimeMillis();
        String date = getDate(mils);
        System.out.println(date);
    }
}
