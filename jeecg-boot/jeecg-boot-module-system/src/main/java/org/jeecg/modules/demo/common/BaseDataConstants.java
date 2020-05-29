package org.jeecg.modules.demo.common;


/**
 * ENUM枚举类
 */
public class BaseDataConstants {


    /**
     * 性别
     */
    public static enum sex{

        MAN("1","男"),FEMAN("2","女");

        private sex(String value,String name){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * 退单审核状态
     */
    public static enum BackOrderStatus{
        WAIT(1,"待审核"),AUDIT(2,"审核中"),PASS(3,"审核通过"),NOTPASS(4,"审核不通过");
        private BackOrderStatus(Integer value,String name){
            this.value = value;
            this.name = name;
        }
        private final Integer value;
        private final String name;

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 通用的启用禁用状态
     */
    public static enum EnableState{
        DISABLE("1","禁用"),ENABLE("0","启用");
        private EnableState(String value,String name){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
    /**
     * 通用的审批状态
     */
    public static enum ApproveState{
        REJECT(0,"不通过"),PASS(1,"通过");
        private ApproveState(Integer value,String name){
            this.value = value;
            this.name = name;
        }
        private final Integer value;
        private final String name;

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
    /**
     * 通用的是否
     */
    public static enum YesOrNo{
        NO(0,"否"),YES(1,"是");
        private YesOrNo(Integer value,String name){
            this.value = value;
            this.name = name;
        }
        private final Integer value;
        private final String name;

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 通用是否删除
     */
    public static enum DeleteFlag{
        NODELETE("1","已删除"),DELETE("0","正常");
        private DeleteFlag(String value,String name){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }




    public static void main(String[] args) {
        String key = BaseDataConstants.DeleteFlag.NODELETE.getName();
        String value = BaseDataConstants.DeleteFlag.NODELETE.getValue();
        System.out.println(key+":"+ value);

    }
}
