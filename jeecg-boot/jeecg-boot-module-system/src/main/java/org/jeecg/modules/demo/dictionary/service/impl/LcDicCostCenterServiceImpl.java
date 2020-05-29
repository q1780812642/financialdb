package org.jeecg.modules.demo.dictionary.service.impl;

import org.jeecg.modules.demo.dictionary.entity.LcDicCostCenter;
import org.jeecg.modules.demo.dictionary.mapper.LcDicCostCenterMapper;
import org.jeecg.modules.demo.dictionary.service.ILcDicCostCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 成本中心
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class LcDicCostCenterServiceImpl extends ServiceImpl<LcDicCostCenterMapper, LcDicCostCenter> implements ILcDicCostCenterService {

    @Autowired
    private LcDicCostCenterMapper lcDicCostCenterMapper;


    @Override
    public String queryLastCode() {
        return lcDicCostCenterMapper.queryLastCode();
    }
}
