package org.jeecg.modules.demo.dictionary.service;

import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 银行卡号管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
public interface ILcDicBankCardService extends IService<LcDicBankCard> {

	public List<LcDicBankCard> selectByMainId(Integer mainId);
}
