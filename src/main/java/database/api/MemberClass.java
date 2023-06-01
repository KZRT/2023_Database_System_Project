package database.api;

// max = Unsigend int max
public enum MemberClass implements DataType {
    BRONZE(0L, 10000L), SILVER(10000L, 50000L), GOLD(50000L, 100000L), PLATINUM(100000L, 500000L),
    DIAMOND(500000L, 1000000L), VIP(1000000L, Integer.MAX_VALUE * 2L);
    private final long pointMin;
    private final long pointMax;

    MemberClass(long pointMin, long pointMax) {
        this.pointMin = pointMin;
        this.pointMax = pointMax;
    }

    public static MemberClass getMemberClass(long point) {
        for (MemberClass ageRange : MemberClass.values()) {
            if (ageRange.pointMin <= point && point < ageRange.pointMax) {
                return ageRange;
            }
        }
        return null;
    }

    @Override
    public DataType getRange(long specificValue) {
        return getMemberClass(specificValue);
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getType() {
        return "MemberClass";
    }

    public long getMaxPoint() {
        return pointMax - 1;
    }

    public long getMinPoint() {
        return pointMin;
    }


}
