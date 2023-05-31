public interface ShoppingMallDatabaseTestAPI extends ShoppingMallDatabaseAPI {
    public boolean testCreatedTable();

    public boolean testInsertedData();

    public boolean testCreatedBitmapIndex();

    public boolean testSelectedDatatype(DataType dataType, User[] output);

    public boolean testCountDatatype(DataType dataType, long output);

    public boolean testSelectedDatatypeWithOperation(DataType[] dataType, Operation[] operation, User[] output);

    public boolean testCountDatatypeWithOperation(DataType[] dataType, Operation[] operation, long output);
}

