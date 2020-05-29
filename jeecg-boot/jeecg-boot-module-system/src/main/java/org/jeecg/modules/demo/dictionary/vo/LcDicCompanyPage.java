package org.jeecg.modules.demo.dictionary.vo;

import java.util.List;
import org.jeecg.modules.demo.dictionary.entity.LcDicCompany;
import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 公司管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@Data
@ApiModel(value="lc_dic_companyPage对象", description="公司管理")
public class LcDicCompanyPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
	private java.lang.String id;
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
	/**公司编码*/
	@Excel(name = "公司编码", width = 15)
	@ApiModelProperty(value = "公司编码")
	private java.lang.String cyCode;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
	@ApiModelProperty(value = "公司名称")
	private java.lang.String cyName;
	/**公司地址*/
	@Excel(name = "公司地址", width = 15)
	@ApiModelProperty(value = "公司地址")
	private java.lang.String address;
	/**公司联系人*/
	@Excel(name = "公司联系人", width = 15)
	@ApiModelProperty(value = "公司联系人")
	private java.lang.String contact;
	/**联系人电话*/
	@Excel(name = "联系人电话", width = 15)
	@ApiModelProperty(value = "联系人电话")
	private java.lang.String phone;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
	private java.lang.String notes;
	/**公司税号*/
	@Excel(name = "公司税号", width = 15)
	@ApiModelProperty(value = "公司税号")
	private java.lang.String ein;
	/**公司类型*/
	@Excel(name = "公司类型", width = 15)
	@ApiModelProperty(value = "公司类型")
	private java.lang.String types;
	/**是否删除*/
	@Excel(name = "是否删除", width = 15)
	@ApiModelProperty(value = "是否删除")
	private java.lang.String delFlag;
	
	@ExcelCollection(name="银行卡号管理")
	@ApiModelProperty(value = "银行卡号管理")
	private List<LcDicBankCard> lcDicBankCardList;
	
}
