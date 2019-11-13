import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: created by ksymmy@163.com at 2019/11/12 18:44
 * @desc:
 */
public class CalendarTest {
    public static void main(String[] args) throws ParseException {
        FastDateFormat instance = FastDateFormat.getInstance("yyyy-MM-dd");
        Date birthday = instance.parse("2018-10-24");
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthday);
        cal.add(Calendar.MONTH, 2);
        Date examDate = cal.getTime();
        System.out.println(examDate.toLocaleString());
    }
}
