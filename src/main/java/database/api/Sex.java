package database.internal;

public enum Sex implements DataType {
    NOT_KNOWN((short) 0), MALE((short) 1), FEMALE((short) 2), NOT_APPLICABLE((short) 9);
    private final short sexValue;

    Sex(short sexValue) {
        this.sexValue = sexValue;
    }

    public short getSexValue() {
        return sexValue;
    }

    public static Sex getSex(short value) {
        for (Sex sex : Sex.values()){
            if(sex.sexValue == value) return sex;
        }
        return null;
    }

    @Override
    public DataType getRange(long specificValue) {
        short tmp = (short) specificValue;
        return switch (tmp) {
            case 1 -> MALE;
            case 2 -> FEMALE;
            case 9 -> NOT_APPLICABLE;
            default -> NOT_KNOWN;
        };
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getType() {
        return "Sex";
    }
}
