package org.jeecg.modules.demo.dictionary.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.dictionary.entity.LcDicCity;
import org.jeecg.modules.demo.dictionary.entity.LcDicItem;
import org.jeecg.modules.demo.dictionary.service.ILcDicCityService;

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
 * @Description: 地市管理
 * @Author: jeecg-boot
 * @Date:   2020-05-22
 * @Version: V1.0
 */
@Api(tags="地市管理")
@RestController
@RequestMapping("/dictionary/lcDicCity")
@Slf4j
public class LcDicCityController extends JeecgController<LcDicCity, ILcDicCityService>{
	@Autowired
	private ILcDicCityService lcDicCityService;

	/**
	 * 分页列表查询
	 *
	 * @param lcDicCity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "地市管理-分页列表查询")
	@ApiOperation(value="地市管理-分页列表查询", notes="地市管理-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<?> queryPageList(LcDicCity lcDicCity,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		boolean isParent = true;//是否是初始化查询
		//三目表达式赋值父级CODE
		String parentCode = StringUtils.isNotBlank(lcDicCity.getParentCode()) ? lcDicCity.getParentCode() : "0";

		QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();

		if(StringUtils.isNotBlank(lcDicCity.getCityCode())){
			isParent = false;
			queryWrapper.like("city_code",lcDicCity.getCityCode());
		}

		if(StringUtils.isNotBlank(lcDicCity.getCityName())){
			isParent = false;
			queryWrapper.like("city_name",lcDicCity.getCityName());
		}
		if(StringUtils.isNotBlank(lcDicCity.getLevel())){
			isParent = false;
			queryWrapper.like("level",lcDicCity.getLevel());
		}

		if(isParent){
			//如果没有其他查询条件，则根据父级编码查询。
			queryWrapper.eq("parent_code", parentCode);
		}

		List<LcDicCity> list = lcDicCityService.list(queryWrapper);

		return Result.ok(list);


	}

	/**
	 * 获取子数据
	 * @param lcDicCity
	 * @param req
	 * @return
	 */
	@AutoLog(value = "地市管理-获取子数据")
	@ApiOperation(value="地市管理-获取子数据", notes="地市管理-获取子数据")
	@GetMapping(value = "/childList")
	public Result<?> queryPageList(LcDicCity lcDicCity,HttpServletRequest req) {
		//QueryWrapper<LcDicCity> queryWrapper = QueryGenerator.initQueryWrapper(lcDicCity, req.getParameterMap());
		QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(StringUtils.isNotBlank(lcDicCity.getParentCode()),"parent_code",lcDicCity.getParentCode());
		List<LcDicCity> list = lcDicCityService.list(queryWrapper);
		return Result.ok(list);
	}


	/**
	 *   添加
	 *
	 * @param lcDicCity
	 * @return
	 */
	@AutoLog(value = "地市管理-添加")
	@ApiOperation(value="地市管理-添加", notes="地市管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicCity lcDicCity) {
		lcDicCityService.addLcDicCity(lcDicCity);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param lcDicCity
	 * @return
	 */
	@AutoLog(value = "地市管理-编辑")
	@ApiOperation(value="地市管理-编辑", notes="地市管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicCity lcDicCity) {

		lcDicCityService.updateLcDicCity(lcDicCity);
		return Result.ok("编辑成功!");
	}

	/**
	 * 批量设置城市等级
	 * @param jsonObject
	 * @return
	 */
	@AutoLog(value = "地市管理-批量设置城市等级")
	@ApiOperation(value="地市管理-批量设置城市等级", notes="地市管理-批量设置城市等级")
	@PutMapping(value = "/batchSetLevel")
	public Result<?> batchSetLevel(@RequestBody JSONObject jsonObject) {
		System.out.println(jsonObject);

		String cityIds = jsonObject.getString("cityIds");
		String level = jsonObject.getString("level");

		if(StringUtils.isNotBlank(cityIds) && StringUtils.isNotBlank(level)){
			String[] ids = cityIds.split(",");
			List<LcDicCity> cityList = new ArrayList<>();
			for (String string:ids) {
				LcDicCity byId = lcDicCityService.getById(string);
				if(!("0".equals(byId.getParentCode()) && "1".equals(byId.getHasChild()))){
					//父级不等于0，并且子集不等于1==》表示排除省份
					byId.setLevel(level);
					cityList.add(byId);
				}
			}
			lcDicCityService.updateBatchById(cityList);

		}

		return Result.ok("批量设置城市等级成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "地市管理-通过id删除")
	@ApiOperation(value="地市管理-通过id删除", notes="地市管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		lcDicCityService.deleteLcDicCity(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "地市管理-批量删除")
	@ApiOperation(value="地市管理-批量删除", notes="地市管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lcDicCityService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "地市管理-通过id查询")
	@ApiOperation(value="地市管理-通过id查询", notes="地市管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LcDicCity lcDicCity = lcDicCityService.getById(id);
		if(lcDicCity==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicCity);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param lcDicCity
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, LcDicCity lcDicCity) {
		return super.exportXls(request, lcDicCity, LcDicCity.class, "地市管理");
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
		return super.importExcel(request, response, LcDicCity.class);
	}

	/**
	 * 功能描述: <br>
	 * 〈重新导入数据
	 *   原数据无主键，以方法重新导入生成主键〉
	 * @date 2020/5/22 11:19
	 * @Author: CuiHonglei
	 * @Param: []
	 * @Return: org.jeecg.common.api.vo.Result<?>
	 */
	@AutoLog(value = "地市管理-重新导入数据")
	@ApiOperation(value="地市管理-重新导入数据", notes="地市管理-重新导入数据")
	@GetMapping(value = "/reData")
	public Result<?> reData() {
		QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();
		List<LcDicCity> list = lcDicCityService.list(queryWrapper);
		lcDicCityService.deleteAll();
		lcDicCityService.savelist(list);
		return Result.ok();
	}

	/**
	 * 功能描述: <br>
	 * 〈更新全表是否存在下级状态〉
	 * @date 2020/5/22 14:49
	 * @Author: CuiHonglei
	 * @Param: []
	 * @Return: org.jeecg.common.api.vo.Result<?>
	 */
	@AutoLog(value = "地市管理-批量更新是否存在下级状态")
	@ApiOperation(value="地市管理-批量更新是否存在下级状态", notes="地市管理-批量更新是否存在下级状态")
	@GetMapping(value = "/updateHasChild")
	public Result<?> updateHasChild() {
		QueryWrapper<LcDicCity> queryWrapper = new QueryWrapper<>();
		lcDicCityService.updateHasChild(lcDicCityService.list(queryWrapper));
		return Result.ok();
	}

}
