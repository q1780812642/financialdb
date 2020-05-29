package org.jeecg.modules.demo.dictionary.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.dictionary.entity.LcDicCity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 地市管理
 * @Author: jeecg-boot
 * @Date:   2020-05-22
 * @Version: V1.0
 */
public interface LcDicCityMapper extends BaseMapper<LcDicCity> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id, @Param("status") String status);

	/*
	 * 功能描述: <br>
	 * 〈删除所有数据〉
	 * @date 2020/5/22 11:23
	 * @Author: CuiHonglei
	 * @Param: []
	 * @Return: void
	 */
	void deleteAll();

	/**
	 * 功能描述: <br>
	 * 〈查询子城市数量〉
	 * @date 2020/5/22 15:22
	 * @Author: CuiHonglei
	 * @Param: [parentCode]
	 * @Return: java.lang.Integer
	 */
	Integer queryParentCount(String parentCode);
}
