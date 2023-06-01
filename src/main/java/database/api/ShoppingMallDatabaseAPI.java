package database.internal;

import java.util.ArrayList;

public interface ShoppingMallDatabaseAPI {
    public void createTable();

    public void insertData(int size);

    public void createBitmapIndex();

    public ArrayList<User> selectDatatype(DataType dataType);

    public long countDatatype(DataType dataType);

    public ArrayList<User> selectDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations);

    public long countDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations);
}

