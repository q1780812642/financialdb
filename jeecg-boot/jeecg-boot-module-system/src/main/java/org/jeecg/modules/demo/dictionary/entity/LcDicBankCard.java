package org.jeecg.modules.demo.dictionary.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 银行卡号管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@ApiModel(value="lc_dic_company对象", description="公司管理")
@Data
@TableName("lc_dic_bank_card")
public class LcDicBankCard implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键")
	private java.lang.Integer id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
	private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
	private java.util.Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	private java.lang.String sysOrgCode;
	/**账户名称*/
	@Excel(name = "账户名称", width = 15)
	@ApiModelProperty(value = "账户名称")
	private java.lang.String accountName;
	/**账号*/
	@Excel(name = "账号", width = 15)
	@ApiModelProperty(value = "账号")
	private java.lang.String cardNo;
	/**开户行*/
	@Excel(name = "开户行", width = 15)
	@ApiModelProperty(value = "开户行")
	private java.lang.String openBank;
	/**联行号*/
	@Excel(name = "联行号", width = 15)
	@ApiModelProperty(value = "联行号")
	private java.lang.String unionpayNo;
	/**开户行省份*/
	@Excel(name = "开户行省份", width = 15)
	@ApiModelProperty(value = "开户行省份")
	private java.lang.String openProvince;
	/**开户行城市*/
	@Excel(name = "开户行城市", width = 15)
	@ApiModelProperty(value = "开户行城市")
	private java.lang.String openCity;
	/**类型*/
	@Excel(name = "类型", width = 15, dicCode = "bank_type")
	@Dict(dicCode = "bank_type")
	@ApiModelProperty(value = "类型")
	private java.lang.String types;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@ApiModelProperty(value = "是否删除")
	private java.lang.String delFlag;
	/**所属公司*/
	@Excel(name = "所属公司", width = 15, dicCode = "bank_type")
	@Dict(dictTable = "lc_dic_company", dicCode = "id", dicText = "cy_name")
	@ApiModelProperty(value = "所属公司")
	private java.lang.Integer companyId;
}
