package org.jeecg.modules.demo.dictionary.service;

import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import org.jeecg.modules.demo.dictionary.entity.LcDicCompany;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 公司管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
public interface ILcDicCompanyService extends IService<LcDicCompany> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(LcDicCompany lcDicCompany,List<LcDicBankCard> lcDicBankCardList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(LcDicCompany lcDicCompany,List<LcDicBankCard> lcDicBankCardList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (Integer id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	/**
	 * 功能描述: <br>
	 * 〈查询上次使用编码〉
	 * @date 2020/5/27 15:35
	 * @Author: CuiHonglei
	 * @Param: []
	 * @Return: java.lang.String
	 */
	public String queryLastCode();
	
}
