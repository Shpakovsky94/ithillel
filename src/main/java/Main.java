import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Date startTime = new Date();

        try {
            long sleepTime;
            int count = 0;

            if (args.length > 0) {
                sleepTime = Long.parseLong(args[0]);
            } else {
                sleepTime = 1000L;
            }
            log.info("Execution started at: {} ", startTime);
            log.info("Sleep param: {} ", sleepTime);

            while (true) {
                Thread.sleep(sleepTime);
                log.info("Count: {} Time from start: {} ms", ++count, getDifferenceFromDateToNowInMs(startTime));
            }
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName(), e.getMessage(), e);
        }

        log.info("Execution took: {} ms", getDifferenceFromDateToNowInMs(startTime));
    }

    private static long getDifferenceFromDateToNowInMs(Date date){
        Date now = new Date();
        return now.getTime() - date.getTime();
    }
}
