package org.jeecg.modules.demo.dictionary.service;

import org.jeecg.modules.demo.dictionary.entity.LcDicCity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;

import java.util.List;

/**
 * @Description: 地市管理
 * @Author: jeecg-boot
 * @Date:   2020-05-22
 * @Version: V1.0
 */
public interface ILcDicCityService extends IService<LcDicCity> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addLcDicCity(LcDicCity lcDicCity);
	
	/**修改节点*/
	void updateLcDicCity(LcDicCity lcDicCity) throws JeecgBootException;
	
	/**删除节点*/
	void deleteLcDicCity(String id) throws JeecgBootException;

	/**
	 * 功能描述: <br>
	 * 〈删除所有数据〉
	 * @date 2020/5/22 11:24
	 * @Author: CuiHonglei
	 * @Param: []
	 * @Return: void
	 */
	void deleteAll();

	/**
	 * 功能描述: <br>
	 * 〈遍历保存数据〉
	 * @date 2020/5/22 13:33
	 * @Author: CuiHonglei
	 * @Param: [list]
	 * @Return: void
	 */
	void savelist(List<LcDicCity> list);

	/**
	 * 功能描述: <br>
	 * 〈批量更新地市信息是否存在下级状态〉
	 * @date 2020/5/22 14:55
	 * @Author: CuiHonglei
	 * @Param: [list]
	 * @Return: void
	 */
	void updateHasChild(List<LcDicCity> list);
}
