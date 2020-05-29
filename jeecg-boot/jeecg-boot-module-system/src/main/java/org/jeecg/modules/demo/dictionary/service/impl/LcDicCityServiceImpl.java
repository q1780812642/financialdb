package org.jeecg.modules.demo.dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.dictionary.entity.LcDicCity;
import org.jeecg.modules.demo.dictionary.mapper.LcDicCityMapper;
import org.jeecg.modules.demo.dictionary.service.ILcDicCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 地市管理
 * @Author: jeecg-boot
 * @Date:   2020-05-22
 * @Version: V1.0
 */
@Service
public class LcDicCityServiceImpl extends ServiceImpl<LcDicCityMapper, LcDicCity> implements ILcDicCityService {

	@Autowired
	private LcDicCityMapper lcDicCityMapper;

	@Override
	public void addLcDicCity(LcDicCity lcDicCity) {
		if(oConvertUtils.isEmpty(lcDicCity.getParentCode())){
			lcDicCity.setParentCode(ILcDicCityService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			//1.查询父节点
			QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq(StringUtils.isNotBlank(lcDicCity.getParentCode()),"city_code",lcDicCity.getParentCode());
			LcDicCity parent = getOne(queryWrapper);
			//LcDicCity parent = baseMapper.selectById(lcDicCity.getParentCode());
			//2.修改父节点信息
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(lcDicCity);
	}

	@Override
	public void updateLcDicCity(LcDicCity lcDicCity) {
		LcDicCity entity = this.getById(lcDicCity.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		//获取原父级编号
		String old_pid = entity.getParentCode();
		//获取修改后的父级编号
		String new_pid = lcDicCity.getParentCode();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				lcDicCity.setParentCode(ILcDicCityService.ROOT_PID_VALUE);
			}
			if(!ILcDicCityService.ROOT_PID_VALUE.equals(lcDicCity.getParentCode())) {
				baseMapper.updateTreeNodeStatus(lcDicCity.getParentCode(), ILcDicCityService.HASCHILD);
			}
		}
		baseMapper.updateById(lcDicCity);
	}
	
	@Override
	public void deleteLcDicCity(String id) throws JeecgBootException {
		LcDicCity lcDicCity = this.getById(id);
		if(lcDicCity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		updateOldParentNode(lcDicCity.getParentCode());
		baseMapper.deleteById(id);
	}

	@Override
	public void deleteAll() {
		lcDicCityMapper.deleteAll();
	}

	@Override
	public void savelist(List<LcDicCity> list) {
		UUIDGenerator uuidGenerator = new UUIDGenerator();
		for(int i=0;i<list.size();i++){
			list.get(i).setId(uuidGenerator.generate());
		}
		saveBatch(list);
	}

	@Override
	public void updateHasChild(List<LcDicCity> list) {
		QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();
		//遍历list 取地址编号查询是否存在下级地市
		for(int i=0;i<list.size();i++){
			LcDicCity listEntity = list.get(i);
			Integer count = lcDicCityMapper.queryParentCount(listEntity.getCityCode());
			//Integer count = baseMapper.selectCount(queryWrapper.eq("parent_code", listEntity.getCityCode()));
			if(count>0){
				//如果存在修改是否有子节点
				LcDicCity lcDicCity = getById(list.get(i).getId());
				lcDicCity.setHasChild(ILcDicCityService.HASCHILD);
				updateLcDicCity(lcDicCity);
			}
		}

	}


	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!ILcDicCityService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<LcDicCity>().eq("parent_code", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, ILcDicCityService.NOCHILD);
			}
		}
	}

}
