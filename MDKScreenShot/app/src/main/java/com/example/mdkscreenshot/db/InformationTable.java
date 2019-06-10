package com.example.mdkscreenshot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */

@DatabaseTable
public class InformationTable {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "code")
    private String code;

    public InformationTable() {
    }

    public InformationTable(long id, String code) {
        this.id = id;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
