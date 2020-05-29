package org.jeecg.modules.demo.dictionary.service.impl;

import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import org.jeecg.modules.demo.dictionary.mapper.LcDicBankCardMapper;
import org.jeecg.modules.demo.dictionary.service.ILcDicBankCardService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 银行卡号管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@Service
public class LcDicBankCardServiceImpl extends ServiceImpl<LcDicBankCardMapper, LcDicBankCard> implements ILcDicBankCardService {
	
	@Autowired
	private LcDicBankCardMapper lcDicBankCardMapper;
	
	@Override
	public List<LcDicBankCard> selectByMainId(Integer mainId) {
		return lcDicBankCardMapper.selectByMainId(mainId);
	}
}
