package com.example.mdkscreenshot.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public class HttpOrmHelper extends OrmLiteSqliteOpenHelper{
    private static final String TABLE_NAME = "MdkData";
    private static HttpOrmHelper httpOrmHelper;
    private Map<String, Dao> daoMap = new HashMap<>();

    private HttpOrmHelper(Context context){
        super(context,TABLE_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.clearTable(connectionSource, InformationTable.class);
            Log.d("HttpOrmliteHrlper", "onCreate: 运行了....");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.d("HttpOrmliteHrlper", "onCreate:异常了..。。 ");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao getDaos(Class classs) throws SQLException {
        Dao dao = null;
        String name = classs.getSimpleName();
        if (daoMap.containsKey(classs)){
            dao = daoMap.get(name);
        }
        if (dao == null){
            dao = super.getDao(classs);
            daoMap.put(name,dao);
        }
        return dao;
    }

    public static HttpOrmHelper getOrmliteHelper(Context context) {
        Context c = context.getApplicationContext();
        if (httpOrmHelper == null){
            synchronized (HttpOrmHelper.class){
                if (httpOrmHelper == null){
                    httpOrmHelper = new HttpOrmHelper(c);
                }
            }
        }
        return httpOrmHelper;
    }

    @Override
    public void close() {
        super.close();
        for (String key :daoMap.keySet()) {
            Dao daos = daoMap.get(key);
            daos = null;
        }
    }
}
