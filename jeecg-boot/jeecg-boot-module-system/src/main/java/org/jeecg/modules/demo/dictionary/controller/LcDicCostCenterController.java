package org.jeecg.modules.demo.dictionary.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.common.BaseDataConstants;
import org.jeecg.modules.demo.common.RedisConstants;
import org.jeecg.modules.demo.dictionary.entity.LcDicCostCenter;
import org.jeecg.modules.demo.dictionary.service.ILcDicCostCenterService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 成本中心
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Api(tags="成本中心")
@RestController
@RequestMapping("/dictionary/lcDicCostCenter")
@Slf4j
public class LcDicCostCenterController extends JeecgController<LcDicCostCenter, ILcDicCostCenterService> {
	@Autowired
	private ILcDicCostCenterService lcDicCostCenterService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 功能描述: <br>
	 * 〈分页列表查询〉
	 * @date 2020/5/28 14:10
	 * @Author: CuiHonglei
	 * @Param: [lcDicCostCenter, pageNo, pageSize, req]
	 * @Return: org.jeecg.common.api.vo.Result<?>
	 */
	@AutoLog(value = "成本中心-分页列表查询")
	@ApiOperation(value="成本中心-分页列表查询", notes="成本中心-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcDicCostCenter lcDicCostCenter,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LcDicCostCenter> queryWrapper = new QueryWrapper<>();
		Page<LcDicCostCenter> page = new Page<LcDicCostCenter>(pageNo, pageSize);
		queryWrapper.eq("del_flag", BaseDataConstants.DeleteFlag.DELETE.getValue());//是否删除
		queryWrapper.like(StringUtils.isNotBlank(lcDicCostCenter.getCostCode()),"cost_code",lcDicCostCenter.getCostCode());//成本中心编码
		queryWrapper.like(StringUtils.isNotBlank(lcDicCostCenter.getCostName()),"cost_name",lcDicCostCenter.getCostName());//成本中心民称
		IPage<LcDicCostCenter> pageList = lcDicCostCenterService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 功能描述: <br>
	 * 〈添加〉
	 * @date 2020/5/28 14:10
	 * @Author: CuiHonglei
	 * @Param: [lcDicCostCenter]
	 * @Return: org.jeecg.common.api.vo.Result<?>
	 */
	@AutoLog(value = "成本中心-添加")
	@ApiOperation(value="成本中心-添加", notes="成本中心-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicCostCenter lcDicCostCenter) {

		//从Redis中读取成本中心编码
		String CostCode = (redisUtil.get(RedisConstants.DICT_CostCode) == null) ? "" : redisUtil.get(RedisConstants.DICT_CostCode).toString();
		int max = 1000;
		int min = 100;
		Random random = new Random();
		if(StringUtils.isEmpty(CostCode)){
			//如果没取到，从数据库查询，并放到Redis中
			CostCode = YouBianCodeUtil.getNextYouBianCode(lcDicCostCenterService.queryLastCode());
		}else{
			//取到直接使用
			CostCode = YouBianCodeUtil.getNextYouBianCode(CostCode);
		}
		//将本次使用编码存入Redis中
		redisUtil.set(RedisConstants.DICT_CostCode, CostCode, random.nextInt(max)%(max-min+1) + min);
		lcDicCostCenter.setCostCode(CostCode);
		lcDicCostCenter.setIzEnable(BaseDataConstants.EnableState.ENABLE.getValue());
		lcDicCostCenterService.save(lcDicCostCenter);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param lcDicCostCenter
	 * @return
	 */
	@AutoLog(value = "成本中心-编辑")
	@ApiOperation(value="成本中心-编辑", notes="成本中心-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicCostCenter lcDicCostCenter) {
		lcDicCostCenterService.updateById(lcDicCostCenter);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "成本中心-通过id删除")
	@ApiOperation(value="成本中心-通过id删除", notes="成本中心-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
		LcDicCostCenter lcDicCostCenter = lcDicCostCenterService.getById(id);
		lcDicCostCenter.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
		lcDicCostCenterService.updateById(lcDicCostCenter);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "成本中心-批量删除")
	@ApiOperation(value="成本中心-批量删除", notes="成本中心-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		for(String id : ids.split(",")){
			LcDicCostCenter lcDicCostCenter = lcDicCostCenterService.getById(id);
			lcDicCostCenter.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
			lcDicCostCenterService.updateById(lcDicCostCenter);
		}
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "成本中心-通过id查询")
	@ApiOperation(value="成本中心-通过id查询", notes="成本中心-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Integer id) {
		LcDicCostCenter lcDicCostCenter = lcDicCostCenterService.getById(id);
		if(lcDicCostCenter==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicCostCenter);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lcDicCostCenter
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LcDicCostCenter lcDicCostCenter) {
        return super.exportXls(request, lcDicCostCenter, LcDicCostCenter.class, "成本中心");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, LcDicCostCenter.class);
    }

}
