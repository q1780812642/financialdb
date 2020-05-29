package org.jeecg.modules.demo.dictionary.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.demo.dictionary.entity.LcDicCompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 公司管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
public interface LcDicCompanyMapper extends BaseMapper<LcDicCompany> {

    /**
     * 功能描述: <br>
     * 〈查询上次使用编码〉
     * @date 2020/5/27 15:34
     * @Author: CuiHonglei
     * @Param: []
     * @Return: java.lang.String
     */
    public String queryLastCode();

}
