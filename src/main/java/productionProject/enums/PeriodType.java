package productionProject.enums;

public enum PeriodType {

    daily(0),
    weekly(1),
    monthly(2),
    permanent(3);

    final int value;


    public int getValue() {
        return value;
    }

    PeriodType(final int value) {
        this.value = value;
    }

//    static PeriodType convertFrom(int value) {
//        switch (value) {
//            case daily.getValue(): return daily;
//            case weekly.getValue(): return weekly;
//            case monthly.getValue(): return monthly;
//            case permanent.getValue(): return permanent;
//            default:
//                throw new IllegalArgumentException("illegal limitPeriod value: " + value);
//        }
//    }
}
