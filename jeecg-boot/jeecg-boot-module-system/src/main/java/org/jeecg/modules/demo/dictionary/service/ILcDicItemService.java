package org.jeecg.modules.demo.dictionary.service;

import org.jeecg.modules.demo.dictionary.entity.LcDicItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.dictionary.vo.LcDicItemDto;

import java.util.List;

/**
 * @Description: 项目管理
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface ILcDicItemService extends IService<LcDicItem> {

    /**
     * 功能描述: <br>
     * 〈查询上次编码〉
     * @date 2020/5/25 11:29
     * @Author: CuiHonglei
     * @Param: []
     * @Return: java.lang.String
     */
    String queryLastCode();

    /**
     * 功能描述: <br>
     * 〈关联查询返回项目管理数据〉
     * @date 2020/5/25 11:29
     * @Author: CuiHonglei
     * @Param: [lcDicItem]
     * @Return: java.util.List<org.jeecg.modules.demo.dictionary.entity.LcDicItem>
     */
    List<LcDicItemDto> queryItem(LcDicItemDto lcDicItemDto);

    /**
     * 功能描述: <br>
     * 〈关联查询返回项目管理数据条数〉
     * @date 2020/5/25 18:04
     * @Author: CuiHonglei
     * @Param: [lcDicItemDto]
     * @Return: java.lang.Integer
     */
    Long queryItemCount(LcDicItemDto lcDicItemDto);
}
