package com.example.xyidentification.utils;

/**
 * 类说明：
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 */
public class ListItem {
    private int image;
    private String ItemString;

    public ListItem(int image, String itemString) {
        this.image = image;
        ItemString = itemString;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getItemString() {
        return ItemString;
    }

    public void setItemString(String itemString) {
        ItemString = itemString;
    }
}
