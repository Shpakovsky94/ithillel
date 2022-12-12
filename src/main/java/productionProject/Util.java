package productionProject;

import java.util.Date;

public class Util {

    private static final String END_TIME_MODE_NEXTDAY = "nextDays";

    public static DatePair calculateCurrentPeriod(
        final String periodType,
        final Date setTime,
        final Boolean renew,
        final String endTimeMode,
        final Integer weekStartFrom
    ) {
        notNull(periodType, "periodType is required");
        notNull(setTime, "setTime is required");

        Date now = new Date();

        isTrue(now.after(setTime), "setTime has to be before now");

        DatePair datePair;
        if (renew != null && renew) {
            // renew is true

            // calculate the start and end time
            if (END_TIME_MODE_NEXTDAY.equals(endTimeMode)) {
                datePair = LimitDatePairNextDaysCalculatorFactory.getInstance(periodType).calculate(setTime, now);
            } else {
                datePair = LimitDatePairFromMorningCalculatorFactory.getInstance(periodType).calculate(setTime, now, weekStartFrom);
            }
        } else {
            // renew is false

            // 1. calculate the start/end time with currentTime = setTime (get first time period)
            // 2. check if now is in the range of (startTime, endTime)
            if (END_TIME_MODE_NEXTDAY.equals(endTimeMode)) {
                datePair = LimitDatePairNextDaysCalculatorFactory.getInstance(periodType).calculate(setTime, setTime);
            } else {
                datePair = LimitDatePairFromMorningCalculatorFactory.getInstance(periodType)
                                                                    .calculate(setTime, setTime, weekStartFrom);
            }
        }

        return datePair;
    }

    public static String createInClauseForStatement(int size) {
        String result = " (";
        for (int i = 0; i < size; i++) {
            result += "?,";
        }
        result = result.substring(0, result.length() - 1);
        result += ")";
        return result;
    }
}
