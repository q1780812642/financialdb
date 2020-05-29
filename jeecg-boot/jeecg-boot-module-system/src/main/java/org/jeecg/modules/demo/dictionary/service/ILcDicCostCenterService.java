package org.jeecg.modules.demo.dictionary.service;

import org.jeecg.modules.demo.dictionary.entity.LcDicCostCenter;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 成本中心
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
public interface ILcDicCostCenterService extends IService<LcDicCostCenter> {
    /**
     * 功能描述: <br>
     * 〈查询成本中心上次编号〉
     * @date 2020/5/28 13:59
     * @Author: CuiHonglei
     * @Param: []
     * @Return: java.lang.String
     */
    String queryLastCode();
}
