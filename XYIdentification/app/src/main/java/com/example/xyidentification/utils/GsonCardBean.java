package com.example.xyidentification.utils;

import java.util.List;

/**
 * 类说明：名片读取Gson解析Json javaBean
 * 公司：武汉玛迪卡智能科技有限公司
 * 作者：lid
 * 时间：2019/0x/xx xx:xx
 * Json:{
 *     message : {
 *                  "status":
 *                  "value":""
 *                }
 *     cardsinfo :
 *     [
 *          {
 *                "type":"20",
 *                "items":
 *                [
 *                   {
 *                      "nID":"",
 *                      "index":"",
 *                      "desc":"",
 *                      "content":""
 *                   }
 *                ]
 *          }
 *     ]
 *
 * }
 */
public class GsonCardBean {
    private MessageBean message;
    private List<CardsinfoBean> cardsinfo;

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public List<CardsinfoBean> getCardsinfo() {
        return cardsinfo;
    }

    public void setCardsinfo(List<CardsinfoBean> cardsinfo) {
        this.cardsinfo = cardsinfo;
    }

    /**
     * status :
     * value :
     */
    public static class MessageBean {
        private int status;
        private String value;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    /**
     * type :
     * items : []
     */
    public static class CardsinfoBean {
        private String type;
        private List<ItemsBean> items;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        /**
         * nID : 0
         * index : 0
         * desc : 姓名
         * content : 李雪夫
         */
        public static class ItemsBean {
            private String nID;
            private String index;
            private String desc;
            private String content;

            public String getNID() {
                return nID;
            }

            public void setNID(String nID) {
                this.nID = nID;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
