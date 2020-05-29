package org.jeecg.modules.demo.dictionary.service.impl;

import org.jeecg.modules.demo.common.BaseDataConstants;
import org.jeecg.modules.demo.dictionary.entity.LcDicCompany;
import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import org.jeecg.modules.demo.dictionary.mapper.LcDicBankCardMapper;
import org.jeecg.modules.demo.dictionary.mapper.LcDicCompanyMapper;
import org.jeecg.modules.demo.dictionary.service.ILcDicCompanyService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 公司管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@Service
public class LcDicCompanyServiceImpl extends ServiceImpl<LcDicCompanyMapper, LcDicCompany> implements ILcDicCompanyService {

	@Autowired
	private LcDicCompanyMapper lcDicCompanyMapper;
	@Autowired
	private LcDicBankCardMapper lcDicBankCardMapper;
	
	@Override
	@Transactional
	public void saveMain(LcDicCompany lcDicCompany, List<LcDicBankCard> lcDicBankCardList) {
		lcDicCompanyMapper.insert(lcDicCompany);
		if(lcDicBankCardList!=null && lcDicBankCardList.size()>0) {
			for(LcDicBankCard entity:lcDicBankCardList) {
				//外键设置
				entity.setDelFlag(BaseDataConstants.DeleteFlag.DELETE.getValue());
				entity.setCompanyId(lcDicCompany.getId());
				lcDicBankCardMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(LcDicCompany lcDicCompany,List<LcDicBankCard> lcDicBankCardList) {
		lcDicCompanyMapper.updateById(lcDicCompany);
		
		//1.先删除子表数据
		lcDicBankCardMapper.deleteByMainId(lcDicCompany.getId());
		
		//2.子表数据重新插入
		if(lcDicBankCardList!=null && lcDicBankCardList.size()>0) {
			for(LcDicBankCard entity:lcDicBankCardList) {
				//外键设置
				entity.setCompanyId(lcDicCompany.getId());
				lcDicBankCardMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(Integer id) {
		lcDicBankCardMapper.deleteByMainId(id);
		LcDicCompany lcDicCompany = getById(id);
		lcDicCompany.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
		updateById(lcDicCompany);
		//lcDicCompanyMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			lcDicBankCardMapper.deleteByMainId(Integer.parseInt(id.toString()));
			LcDicCompany lcDicCompany = getById(id);
			lcDicCompany.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
			updateById(lcDicCompany);
			//lcDicCompanyMapper.deleteById(id);
		}
	}

	@Override
	public String queryLastCode() {
		return lcDicCompanyMapper.queryLastCode();
	}

}
