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

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.common.BaseDataConstants;
import org.jeecg.modules.demo.common.PageList;
import org.jeecg.modules.demo.common.RedisConstants;
import org.jeecg.modules.demo.dictionary.entity.LcDicItem;
import org.jeecg.modules.demo.dictionary.service.ILcDicItemService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.dictionary.vo.LcDicItemDto;
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
 * @Description: 项目管理
 * @Author: chl
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Api(tags="项目管理")
@RestController
@RequestMapping("/dictionary/lcDicItem")
@Slf4j
public class LcDicItemController extends JeecgController<LcDicItem, ILcDicItemService> {
	@Autowired
	private ILcDicItemService lcDicItemService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 分页列表查询
	 *
	 * @param lcDicItemDto
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "项目管理-分页列表查询")
	@ApiOperation(value="项目管理-分页列表查询", notes="项目管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcDicItemDto lcDicItemDto,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		int firstResult = (pageNo-1) * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		lcDicItemDto.setPageNo(firstResult);
		lcDicItemDto.setPageSize(maxResults);
		lcDicItemDto.setDelFlag(BaseDataConstants.DeleteFlag.DELETE.getValue());
		Long count = lcDicItemService.queryItemCount(lcDicItemDto);//获取总条数
		List<LcDicItemDto> list = lcDicItemService.queryItem(lcDicItemDto);//获取分页数据

		//分页数据转换
		IPage<Map<String, Object>> paging = PageList.paging(pageNo, pageSize, count, list);
		return Result.ok(paging);
	}

	/**
	 *   添加
	 *
	 * @param lcDicItem
	 * @return
	 */
	@AutoLog(value = "项目管理-添加")
	@ApiOperation(value="项目管理-添加", notes="项目管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicItem lcDicItem) {
		YouBianCodeUtil youBianCodeUtil = new YouBianCodeUtil();
		//先从redis 中获取公司的当前编码
		String ItemCode = (redisUtil.get(RedisConstants.DICT_ITEMCODE) == null) ? "" : redisUtil.get(RedisConstants.DICT_ITEMCODE).toString();
		int max = 1000;
		int min = 100;
		Random random = new Random();
		if(StringUtils.isEmpty(ItemCode)){
			//如果没取到，取数据库查询，并放到redis中。
			ItemCode = YouBianCodeUtil.getNextYouBianCode(lcDicItemService.queryLastCode());
		}else{
			//如果取到了直接使用。
			ItemCode = YouBianCodeUtil.getNextYouBianCode(ItemCode);
		}
		redisUtil.set(RedisConstants.DICT_ITEMCODE, ItemCode,random.nextInt(max)%(max-min+1) + min);
		lcDicItem.setItemCode(ItemCode);
		lcDicItem.setIzEnable(BaseDataConstants.EnableState.ENABLE.getValue());
		lcDicItemService.save(lcDicItem);
		return Result.ok("添加成功！");
	}



	/**
	 *  编辑
	 *
	 * @param lcDicItem
	 * @return
	 */
	@AutoLog(value = "项目管理-编辑")
	@ApiOperation(value="项目管理-编辑", notes="项目管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicItem lcDicItem) {
		lcDicItemService.updateById(lcDicItem);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "项目管理-通过id删除")
	@ApiOperation(value="项目管理-通过id删除", notes="项目管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		LcDicItem byId = lcDicItemService.getById(id);
		byId.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
		lcDicItemService.updateById(byId);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "项目管理-批量删除")
	@ApiOperation(value="项目管理-批量删除", notes="项目管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lcDicItemService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "项目管理-通过id查询")
	@ApiOperation(value="项目管理-通过id查询", notes="项目管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LcDicItem lcDicItem = lcDicItemService.getById(id);
		if(lcDicItem==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicItem);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param lcDicItem
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, LcDicItem lcDicItem) {
		return super.exportXls(request, lcDicItem, LcDicItem.class, "项目管理");
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
		return super.importExcel(request, response, LcDicItem.class);
	}

}
