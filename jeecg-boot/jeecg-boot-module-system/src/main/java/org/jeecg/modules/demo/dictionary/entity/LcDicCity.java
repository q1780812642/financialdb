package org.jeecg.modules.demo.dictionary.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 地市管理
 * @Author: jeecg-boot
 * @Date:   2020-05-22
 * @Version: V1.0
 */
@Data
@TableName("lc_dic_city")
@ApiModel(value="lc_dic_city对象", description="地市管理")
public class LcDicCity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
    /**地市名称*/
    @Excel(name = "地市名称", width = 15)
    @ApiModelProperty(value = "地市名称")
    private String cityName;
    /**地市编码*/
    @Excel(name = "地市编码", width = 15)
    @ApiModelProperty(value = "地市编码")
    private String cityCode;
    /**父级编码*/
    @Excel(name = "父级编码", width = 15)
    @ApiModelProperty(value = "父级编码")
    private String parentCode;
    /**是否有子节点*/
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    /**城市等级*/
    @Excel(name = "城市等级", width = 15, dicCode = "city_level")
    @Dict(dicCode = "city_level")
    @ApiModelProperty(value = "城市等级")
    private String level;

}
