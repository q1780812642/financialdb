package org.jeecg.modules.demo.dictionary.service.impl;

import org.jeecg.modules.demo.dictionary.entity.LcDicItem;
import org.jeecg.modules.demo.dictionary.mapper.LcDicItemMapper;
import org.jeecg.modules.demo.dictionary.service.ILcDicItemService;
import org.jeecg.modules.demo.dictionary.vo.LcDicItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 项目管理
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Service
public class LcDicItemServiceImpl extends ServiceImpl<LcDicItemMapper, LcDicItem> implements ILcDicItemService {

    @Autowired
    private LcDicItemMapper lcDicItemMapper;

    @Override
    public String queryLastCode() {
        return lcDicItemMapper.queryLastCode();
    }

    @Override
    public List<LcDicItemDto> queryItem(LcDicItemDto lcDicItemDto) {
        return lcDicItemMapper.queryItem(lcDicItemDto);
    }

    @Override
    public Long queryItemCount(LcDicItemDto lcDicItemDto) {
        return lcDicItemMapper.queryItemCount(lcDicItemDto);
    }
}
