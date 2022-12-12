package productionProject.service;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateService {

    public static Date getCurrentPeriodEndTime(final String periodType) {
        Date now                  = getCurrentDatetime();
        Date currentPeriodEndTime = null;

        switch (periodType) {
            case "daily":
                currentPeriodEndTime = Dates.setToMidnight(Dates.addDays(now, 1));
                break;
            case "weekly":
                currentPeriodEndTime = Dates.setToMidnight(Dates.addWeeks(now, 1));
                break;
            case "monthly":
                currentPeriodEndTime = Dates.setToMidnight(Dates.addMonths(now, 1));
                break;
            default:
                log.debug("Unexpected value: " + periodType);
        }
        return currentPeriodEndTime;
    }

    public static Date getCurrentDatetime() {
        return new java.util.Date();
    }

    public static java.sql.Date convertUtilDateToSqlDate(final java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

}
