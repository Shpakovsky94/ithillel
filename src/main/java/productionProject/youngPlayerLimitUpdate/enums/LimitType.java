package productionProject.youngPlayerLimitUpdate.enums;

public enum LimitType {

    /**
     * used for deposit limits
     */
    deposit(1),

    /**
     * Player's loss limit over the period
     */

    loss(3),

    /**
     * Session time limit
     */
    session(13);


    final int value;

    public int getValue() {
        return value;
    }

    LimitType(final int value) {
        this.value = value;
    }

    static LimitType valueOf(final int val) {
        switch (val) {
            case 1:
                return deposit;
            case 3:
                return loss;
            case 13:
                return session;
            default:
                throw new IllegalArgumentException("invalid enum value for LimitType: " + val);
        }
    }
}
