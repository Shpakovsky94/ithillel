package productionProject.youngPlayerLimitUpdate.enums;

public enum Activity {
    /**
     * Activity created when bo person removes player's gaming limit
     */
    RemoveGamingLimit(21),
    /**
     * Player activity for creating a new session time limit
     */
    CreateSessionTimeLimit(74),
    /**
     * Player activity for removing session time limit
     */
    ModifySessionTimeLimit(75),
    /**
     * Player activity for removing session time limit
     */
    RemoveSessionTimeLimit(76),
    /**
     * Activity created when player modifies loss limit
     */
    ModifyLossLimit(118);

    final int value;

    public int getValue() {
        return value;
    }


    Activity(final int value) {
        this.value = value;
    }

    static Activity valueOf(final int val) {
        switch (val) {
            case 21:
                return RemoveGamingLimit;
            case 74:
                return CreateSessionTimeLimit;
            case 75:
                return ModifySessionTimeLimit;
            case 76:
                return RemoveSessionTimeLimit;
            case 118:
                return ModifyLossLimit;
            default:
                throw new IllegalArgumentException("invalid enum value for Activity: " + val);
        }
    }

}
