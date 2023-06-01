package database.internal;

import java.util.Arrays;

public class User implements Comparable<User> {
    private long memberNumber;
    private String id;
    private byte[] password;
    private String name;
    private String address;
    private short sex;
    private String phoneNumber;
    private int age;
    private long point;

    public User(long memberNumber, String id, byte[] password, String name, String address, short sex, String phoneNumber, int age, long point){
        this.memberNumber = memberNumber;
        this.id = id;
        this.password = password;
        this.name = name;
        this.address = address;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.point = point;
    }

    public User(String id, byte[] password, String name, String address, short sex, String phoneNumber, int age, long point){
        this.memberNumber = 0;
        this.id = id;
        this.password = password;
        this.name = name;
        this.address = address;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.point = point;
    }

    public String getUserInformation(){
        return (memberNumber + " " + id + " " + Arrays.toString(password) + " " + name + " " + address + " " + sex + " " + phoneNumber + " " + age + " " + point);
    }

    public String getInsertQuery(){
        return ("'" + id + "', " + Arrays.toString(password) + ", '" + name + "', '" + address + "', " + sex + ", '" + phoneNumber + "', " + age + ", " + point);
    }

    @Override
    public int compareTo(User user) {
        return Long.compare(this.memberNumber, user.memberNumber);
    }

    public long getMemberNumber() {
        return memberNumber;
    }

    public String getId() {
        return id;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Sex getSex() {
        return Sex.getSex(sex);
    }

    public short getSexNum(){
        return sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public AgeRange getAgeRange(){
        return AgeRange.getAgeRange((short) age);
    }

    public long getPoint() {
        return point;
    }

    public MemberClass getMemberClass(){
        return MemberClass.getMemberClass(point);
    }
}
