import java.util.ArrayList;

public interface ShoppingMallDatabaseAPI {
    public void createTable();

    public void insertData(int size);

    public void createBitmapIndex();

    public ArrayList<User> selectDatatype(DataType dataType);

    public long countDatatype(DataType dataType);

    public ArrayList<User> selectDatatypeWithOperation(DataType[] dataType, Operation[] operation);

    public long countDatatypeWithOperation(DataType[] dataType, Operation[] operation);
}

