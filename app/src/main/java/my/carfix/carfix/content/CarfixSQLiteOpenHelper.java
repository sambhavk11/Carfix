package my.carfix.carfix.content;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class CarfixSQLiteOpenHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "carfix.db";
    private static final int DATABASE_VERSION = 1;

    private static final String VEHICLE_TABLE_CREATE = "create table vehicle(_id integer primary key autoincrement, regNo text not null, model text not null, phoneNo text not null, service text not null);";

    private static final String VEHICLE_TABLE_DROP = "drop table vehicle";

    private static final String GUIDE_TABLE_CREATE = "create table guide(_id integer primary key autoincrement, remark text not null)";

    private static final String GUIDE_TABLE_DROP = "drop table guide";

    private static final String TRUCK_TABLE_CREATE = "create table truck(_id integer primary key autoincrement, truckNo text not null, userCode text not null, autoCheckInTime integer not null)";

    private static final String TRUCK_TABLE_DROP = "drop table truck";

    private static final String TRUCK_DESTINATION_TABLE_CREATE = "create table truck_destination(_id integer primary key autoincrement, caseNo text not null, regNo text not null, model text not null, phoneNo text not null, service text not null, breakdownAddress text not null, breakdownLatitude real not null, breakdownLongitude real not null, destinationAddress text not null, destinationLatitude real not null, destinationLongitude real not null)";

    private static final String TRUCK_DESTINATION_TABLE_DROP = "drop table truck_destination";

    private static final String CASE_TABLE_CREATE = "create table log_case(_id integer primary key autoincrement, caseNo not null, truckNo text not null, arrivedTime integer not null, location text not null, driverName text not null, imageURL text not null)";

    private static final String CASE_TABLE_DROP = "drop table log_case";

    private static final String CASE_MESSAGE_TABLE_CREATE = "create table log_case_message(_id integer primary key autoincrement, caseNo text not null, message text not null, messageTime integer not null)";

    private static final String CASE_MESSAGE_TABLE_DROP = "drop table log_case_message";

    public CarfixSQLiteOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(VEHICLE_TABLE_CREATE);
        database.execSQL(GUIDE_TABLE_CREATE);
        database.execSQL(TRUCK_TABLE_CREATE);
        database.execSQL(TRUCK_DESTINATION_TABLE_CREATE);
        database.execSQL(CASE_TABLE_CREATE);
        database.execSQL(CASE_MESSAGE_TABLE_CREATE);
    }

    private void updateDatabase(SQLiteDatabase database)
    {
        try
        {
            database.execSQL(VEHICLE_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        try
        {
            database.execSQL(GUIDE_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        try
        {
            database.execSQL(TRUCK_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        try
        {
            database.execSQL(TRUCK_DESTINATION_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        try
        {
            database.execSQL(CASE_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        try
        {
            database.execSQL(CASE_MESSAGE_TABLE_DROP);
        }
        catch (Exception e)
        {
            ;
        }

        database.execSQL(VEHICLE_TABLE_CREATE);
        database.execSQL(GUIDE_TABLE_CREATE);
        database.execSQL(TRUCK_TABLE_CREATE);
        database.execSQL(TRUCK_DESTINATION_TABLE_CREATE);
        database.execSQL(CASE_TABLE_CREATE);
        database.execSQL(CASE_MESSAGE_TABLE_CREATE);
    }
    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        updateDatabase(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        updateDatabase(database);
    }
}
