package org.jeecg.modules.demo.dictionary.mapper;

import java.util.List;
import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 银行卡号管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
public interface LcDicBankCardMapper extends BaseMapper<LcDicBankCard> {

	public boolean deleteByMainId(@Param("mainId") Integer mainId);
    
	public List<LcDicBankCard> selectByMainId(@Param("mainId") Integer mainId);
}
