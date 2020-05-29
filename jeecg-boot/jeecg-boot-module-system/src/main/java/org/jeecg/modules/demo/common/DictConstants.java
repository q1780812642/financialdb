package org.jeecg.modules.demo.common;

/**
 * 字典信息
 * @date 2020/5/29 11:28
 * @Author: CuiHonglei
 */
public interface DictConstants {

    /**
     * 性别
     */
    /*** 男 ***/
    String DICT_MAN = "1";
    /*** 女 ***/
    String DICT_FEMAN = "2";


    /**
     * 退单审核状态
     */
    /*** 待审核 ****/
    Integer BANK_WAIT = 1;
    /*** 审核中 ****/
    Integer BANK_AUDIT = 1;
    /*** 审核通过 ****/
    Integer BANK_PASS = 1;
    /*** 审核不通过 ****/
    Integer BANK_NOTPASS = 1;


    /**
     * 通用的启用禁用状态
     */
    /**** 禁用 ****/
    String DICT_DISABLE = "1";
    /**** 启用 ****/
    String DICT_ENABLE = "0";


    /**
     * 通用的审批状态
     */
    /**** 不通过 ****/
    Integer APP_REJECT = 0;
    /**** 通过 ****/
    Integer App_PASS = 1;


    /**
     * 通用的是否
     */
    /**** 不通过 ****/
    Integer DICT_NO = 0;
    /**** 通过 ****/
    Integer DICT_YES = 1;


    /**
     * 通用是否删除
     */
    /**** 不通过 ****/
    String DICT_NODELETE = "1";
    /**** 通过 ****/
    String DICT_DELETE = "0";


}
