/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * @ClassName:
 * @Description: 收货地址
 * @Author Stefan
 * @Date
 */

public class AddressOpenHelper extends SQLiteOpenHelper {

    public AddressOpenHelper(Context context) {
        super(context, "InGymDB.db", null, 1);
    }

    /**
     * Called when the database is created for the first time.
     * 当数据库第一次创建的时候调用
     * 那么这个方法特别适合做表结构的初始化  创建表就是写sql语句
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table address " +
                "(_id integer primary key autoincrement, " +
                "consignee varchar(20), phone varchar(20), " +
                "province varchar(255), detail varchar(255));");
    }

    /**
     * Called when the database needs to be upgraded
     * 当数据库版本升级的时候调用
     * 这个方法适合做表结构的更新
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
