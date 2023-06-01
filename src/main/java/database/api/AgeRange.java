package database.api;

public enum AgeRange implements DataType {
    BABIES((short)0, (short)10), TEEN((short)10, (short)20), TWENTIES((short)20, (short)30),
    THIRTIES((short)30, (short)40), FORTIES((short)40, (short)50), FIFTIES((short)50, (short)60),
    SIXTIES((short)60, (short)70), SEVENTIES((short)70, (short)80), EIGHTIES((short)80, (short)90),
    NINETIES((short)90, (short)100), OVER_HUNDREDS((short)100, Short.MAX_VALUE);
    private final short ageMin;
    private final short ageMax;

    AgeRange(short ageMin, short ageMax) {
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }

    public static AgeRange getAgeRange(short age) {
        for (AgeRange ageRange : AgeRange.values()) {
            if (ageRange.ageMin <= age && age < ageRange.ageMax) {
                return ageRange;
            }
        }
        return null;
    }

    public short getMinAge() {
        return ageMin;
    }

    public short getMaxAge() {
        return (short) (ageMax - 1);
    }

    @Override
    public DataType getRange(long specificValue) {
        return getAgeRange((short) specificValue);
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getType() {
        return "AgeRange";
    }
}
