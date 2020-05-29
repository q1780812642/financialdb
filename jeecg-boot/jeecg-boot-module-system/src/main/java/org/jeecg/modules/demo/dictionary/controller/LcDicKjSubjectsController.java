package org.jeecg.modules.demo.dictionary.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.dictionary.entity.LcDicKjSubjects;
import org.jeecg.modules.demo.dictionary.service.ILcDicKjSubjectsService;

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
 * @Description: 会计科目
 * @Author: zzx
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Api(tags="会计科目")
@RestController
@RequestMapping("/dictionary/lcDicKjSubjects")
@Slf4j
public class LcDicKjSubjectsController extends JeecgController<LcDicKjSubjects, ILcDicKjSubjectsService> {
	@Autowired
	private ILcDicKjSubjectsService lcDicKjSubjectsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param lcDicKjSubjects
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "会计科目-分页列表查询")
	@ApiOperation(value="会计科目-分页列表查询", notes="会计科目-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcDicKjSubjects lcDicKjSubjects,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LcDicKjSubjects> queryWrapper = QueryGenerator.initQueryWrapper(lcDicKjSubjects, req.getParameterMap());
		//queryWrapper.eq("");

		Page<LcDicKjSubjects> page = new Page<LcDicKjSubjects>(pageNo, pageSize);
		IPage<LcDicKjSubjects> pageList = lcDicKjSubjectsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param lcDicKjSubjects
	 * @return
	 */
	@AutoLog(value = "会计科目-添加")
	@ApiOperation(value="会计科目-添加", notes="会计科目-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicKjSubjects lcDicKjSubjects) {
		lcDicKjSubjectsService.save(lcDicKjSubjects);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param lcDicKjSubjects
	 * @return
	 */
	@AutoLog(value = "会计科目-编辑")
	@ApiOperation(value="会计科目-编辑", notes="会计科目-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicKjSubjects lcDicKjSubjects) {
		lcDicKjSubjectsService.updateById(lcDicKjSubjects);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会计科目-通过id删除")
	@ApiOperation(value="会计科目-通过id删除", notes="会计科目-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {

		LcDicKjSubjects byId = lcDicKjSubjectsService.getById(id);
		//byId.setDelFlag();//设置为已删除

		lcDicKjSubjectsService.updateById(byId);
		lcDicKjSubjectsService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "会计科目-批量删除")
	@ApiOperation(value="会计科目-批量删除", notes="会计科目-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lcDicKjSubjectsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会计科目-通过id查询")
	@ApiOperation(value="会计科目-通过id查询", notes="会计科目-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LcDicKjSubjects lcDicKjSubjects = lcDicKjSubjectsService.getById(id);
		if(lcDicKjSubjects==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicKjSubjects);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lcDicKjSubjects
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LcDicKjSubjects lcDicKjSubjects) {
        return super.exportXls(request, lcDicKjSubjects, LcDicKjSubjects.class, "会计科目");
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
        return super.importExcel(request, response, LcDicKjSubjects.class);
    }

}
