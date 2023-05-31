import java.util.ArrayList;

//class Test implements ShoppingMallDatabaseAPI{
//    DataType dataType;
//    public Test(){
//    }
//    @Override
//    public User[] selectDatatype(DataType dataType) {
//        System.out.println(dataType.getClass());
//        if (dataType.getClass() == Sex.class){
//            System.out.println("Sex");
//            System.out.println(dataType);
//        } else if(dataType.getClass() == AgeRange.class){
//            System.out.println("AgeRange");
//            System.out.println(dataType);
//        } else if(dataType.getClass() == MemberClass.class) {
//            System.out.println("MemberClass");
//            System.out.println(dataType);
//        }
//        return null;
//    }
//
//    @Override
//    public long countDatatype(DataType dataType) {
//        System.out.println(dataType.getClass());
//        return 0;
//    }
//}
public class Main {
    public static void main(String[] args) {
        ShoppingMallDatabaseAPI shoppingMallDatabaseAPI = ShoppingMallDatabase.getInstance();
//        shoppingMallDatabaseAPI.createTable();
        ShoppingMallDatabaseTestAPI shoppingMallDatabaseTestAPI = ShoppingMallDatabaseTest.getInstance();
//        System.out.println(shoppingMallDatabaseTestAPI.testCreatedTable());
//        shoppingMallDatabaseAPI.insertData(1000);
//        shoppingMallDatabaseTestAPI.insertData(2000);
//        System.out.println(shoppingMallDatabaseTestAPI.testInsertedData());
        shoppingMallDatabaseAPI.createBitmapIndex();
        ArrayList<User> maleArray = shoppingMallDatabaseAPI.selectDatatype(Sex.MALE);
        ArrayList<User> fiftyArray = shoppingMallDatabaseAPI.selectDatatype(AgeRange.FIFTIES);
        System.out.println(shoppingMallDatabaseTestAPI.testCreatedBitmapIndex());
    }

    public static void test(int i){
        System.out.println(i);
    }
}