package database.internal;

import java.util.ArrayList;

public interface ShoppingMallDatabaseTestAPI extends ShoppingMallDatabaseAPI {
    public boolean testCreatedTable();

    public boolean testInsertedData();

    public boolean testCreatedBitmapIndex();

    public boolean testSelectedDatatype(DataType dataType, User[] output);

    public boolean testCountDatatype(DataType dataType, long output);

    public boolean testSelectedDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, ArrayList<User> output);

    public boolean testCountDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, long output);
}

