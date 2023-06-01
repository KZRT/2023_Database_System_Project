package database.api;

import java.util.ArrayList;

public interface ShoppingMallDatabaseTestAPI {
    public void insertData(int size);

    public boolean testCreatedTable();

    public boolean testInsertedData();

    public boolean testCreatedBitmapIndex();

    public boolean testSelectedDatatype(DataType dataType, ArrayList<User> output);

    public boolean testCountDatatype(DataType dataType, long output);

    public boolean testSelectedDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, ArrayList<User> output);

    public boolean testCountDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, long output);
}

