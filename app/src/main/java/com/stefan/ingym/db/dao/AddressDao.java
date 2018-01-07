package com.stefan.ingym.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stefan.ingym.db.AddressOpenHelper;
import com.stefan.ingym.pojo.mine.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */

public class AddressDao {

    private AddressOpenHelper addressOpenHelper;

    /**
	 * BlackListDao单列模式
	 * 1、私有化构造方法
	 * 2、声明一个当前类的对象
	 * 3、提供一个静态的方法，如果当前类的对象为空，就创建一个新的
	 */

    // [1]私有化构造方法
    private AddressDao(Context context) {
        // 创建数据库及其表结构
        addressOpenHelper = new AddressOpenHelper(context);
    }

    // [2]声明一个当前类的对象（private）
    private static AddressDao addressDao = null;

    // [3]提供一个静态（经常要用类名来调用此方法，就维护成静态方法）的方法，如果当前类的对象为空，就创建一个新的（public）
    public static AddressDao getInstance(Context context) {
        if (addressDao == null)
            addressDao = new AddressDao(context);
        return addressDao;
    }

    /**
     * 插入数据操作
     * @param consignee 收件人
     * @param phone     收件人联系电话
     * @param province  省市地址
     * @param detail    详细地址
     */
    public void insert(String consignee, String phone, String province, String detail) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();
        // 封装数据
        ContentValues values = new ContentValues();
        values.put("consignee", consignee);
        values.put("phone", phone);
        values.put("province", province);
        values.put("detail", detail);
        /**
         * 执行插入操作
		 * insert(table,    将要插入数据的表名
		 * nullColumnHack,  如果对应位置没有数据则维护成null
		 * values)          要插入的数据
		 */
        db.insert("address", null, values);
        // 关闭数据库
        db.close();
    }

    /**
     * 查询所有
     * @return
     */
    public List<Address> findAll() {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();
        // 查询操作
        Cursor cursor = db.query("address", new String[]{"consignee", "phone", "province", "detail"}, null, null, null, null, "_id desc");
        // 创建有序集合
        List<Address> addressLists = new ArrayList<Address>();
        // 拿到游标做向下移动循环查询
        while (cursor.moveToNext()) {
            // 创建Address实体类，用来封装数据
            Address address = new Address();
            // 查询游标所在行的数据,并直接封装进Address对应的属性中
            address.setConsignee(cursor.getString(0));
            address.setPhone(cursor.getString(1));
            address.setProvince(cursor.getString(2));
            address.setDetail(cursor.getString(3));
            // 每循环查询完一次就将该次循环的所有互数据添加到集合
            addressLists.add(address);
        }
        cursor.close();
        db.close();
        // 返回查询完全的所有数据
        return addressLists;
    }

    /**
     * 分页查询，设定每次查询20条数据
     * @param index	查询的索引值
     */
    public ArrayList<Address> find(int index) {
        // 1、开启数据库，为查询做准备
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();

        // 2、进行SQL语句的查询
        Cursor cursor = db.rawQuery("select * from address order by _id desc limit ?,20;", new String[]{index + ""});

        // 6、创建数据集合
        ArrayList<Address> addressLists = new ArrayList<Address>();

        // 3、拿到游标后做一直向下移动查询循环操作
        while (cursor.moveToNext()) {
            // 4、创建Address实体类，用来封装数据
            Address address = new Address();
            // 5、查询游标所在行的数据
            address.setId(cursor.getString(cursor.getColumnIndex("_id")));
            address.setConsignee(cursor.getString(cursor.getColumnIndex("consignee")));
            address.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            address.setProvince(cursor.getString(cursor.getColumnIndex("province")));
            address.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
            // 7、每一次循环都将查询出来的数据添加进集合中去
            addressLists.add(address);
        }
        // 8、游标使用完毕后关闭
        cursor.close();
        // 9、数据库使用完毕后关闭
        db.close();
        // 10、最后返回数据集合
        return addressLists;
    }

    /**
     * 查询数据库中共有几条数据
     * @return	返回数据库中数据总条数，返回0代表没有数据或出现异常
     */
    public int count() {
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from address;", null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        db.close();
        cursor.close();
        return count;
    }

    /**
     * 根据ID更新地址
     * @param _id
     * @param consignee
     * @param phone
     * @param province
     * @param detail
     */
    public void update(String _id, String consignee, String phone, String province, String detail){
        // 开启数据库，为更新做准备
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();
		/*
		 * update(
		 *  table,          要更新的表名
		 *  values,		          要更新数据库中对应的那一列（要去更新的字段的值）
		 *  whereClause,    根据什么来更新（更新的 条件）
		 *  whereArgs)
		 */
        ContentValues values = new ContentValues();
        values.put("consignee", consignee);
        values.put("phone", phone);
        values.put("province", province);
        values.put("detail", detail);
        db.update("address", values, "_id = ?", new String[]{_id});
        //用完关闭
        db.close();
    }

    /**
     * 根据ID去删除数据
     * @param _id
     */
    public void delete(String _id) {
        // 开启数据库，为删除做准备blacklist
        SQLiteDatabase db = addressOpenHelper.getWritableDatabase();
        // 执行删除数据操作
        db.delete("address", "_id = ?", new String[]{_id});
        // 用完关闭
        db.close();
    }

}
