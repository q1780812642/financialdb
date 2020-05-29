package org.jeecg.modules.demo.dictionary.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.dictionary.entity.LcDicCostCenter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 成本中心
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
public interface LcDicCostCenterMapper extends BaseMapper<LcDicCostCenter> {

    /**
     * 功能描述: <br>
     * 〈查询成本中心上次编号〉
     * @date 2020/5/28 13:58
     * @Author: CuiHonglei
     * @Param: []
     * @Return: java.lang.String
     */
    String queryLastCode();
}
