package dixi.bupt.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

/**
 * @author chendixi
 * Created on 2023-06-11
 */
public class CalendarTest {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        long endDate = calendar.getTimeInMillis();

        // 计算开始日期
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        long startDate = calendar.getTimeInMillis();

        // 格式化日期并输出
        String startFormatted = sdf.format(startDate);
        String endFormatted = sdf.format(endDate);

        System.out.println("近7天时间范围：" + startFormatted + " - " + endFormatted);

    }

    @Test
    public void testLatestIndex() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24-10);
        long latestDate = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long lastWeekDate = calendar.getTimeInMillis();

        String latestDateFormatted = sdf.format(latestDate);
        String lastWeekDateFormatted = sdf.format(lastWeekDate);
        System.out.println("周环比日期：" + lastWeekDateFormatted + " - " + latestDateFormatted);
    }
}
