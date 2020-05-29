package org.jeecg.modules.demo.dictionary.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.modules.demo.common.BaseDataConstants;
import org.jeecg.modules.demo.common.DictConstants;
import org.jeecg.modules.demo.common.RedisConstants;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import org.jeecg.modules.demo.dictionary.entity.LcDicCompany;
import org.jeecg.modules.demo.dictionary.vo.LcDicCompanyPage;
import org.jeecg.modules.demo.dictionary.service.ILcDicCompanyService;
import org.jeecg.modules.demo.dictionary.service.ILcDicBankCardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 公司管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@Api(tags="公司管理")
@RestController
@RequestMapping("/dictionary/lcDicCompany")
@Slf4j
public class LcDicCompanyController {
	@Autowired
	private ILcDicCompanyService lcDicCompanyService;
	@Autowired
	private ILcDicBankCardService lcDicBankCardService;
	@Autowired
	private RedisUtil redisUtil;
	
	/**
	 * 功能描述: <br>
	 * 〈分页列表查询〉
	 * @date 2020/5/27 16:53
	 * @Author: CuiHonglei
	 * @Param: [lcDicCompany, pageNo, pageSize, req]
	 * @Return: org.jeecg.common.api.vo.Result<?>
	 */
	@AutoLog(value = "公司管理-分页列表查询")
	@ApiOperation(value="公司管理-分页列表查询", notes="公司管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcDicCompany lcDicCompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<LcDicCompany> queryWrapper = QueryGenerator.initQueryWrapper(lcDicCompany, req.getParameterMap());
		QueryWrapper<LcDicCompany> queryWrapper = new QueryWrapper<>();
		Page<LcDicCompany> page = new Page<LcDicCompany>(pageNo, pageSize);
		queryWrapper.eq("del_flag", BaseDataConstants.DeleteFlag.DELETE.getValue());
		queryWrapper.like(StringUtils.isNotBlank(lcDicCompany.getCyCode()),"cy_code",lcDicCompany.getCyCode());
		queryWrapper.like(StringUtils.isNotBlank(lcDicCompany.getCyName()),"cy_name",lcDicCompany.getCyName());
		IPage<LcDicCompany> pageList = lcDicCompanyService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param lcDicCompanyPage
	 * @return
	 */
	@AutoLog(value = "公司管理-添加")
	@ApiOperation(value="公司管理-添加", notes="公司管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicCompanyPage lcDicCompanyPage) {
		LcDicCompany lcDicCompany = new LcDicCompany();
		BeanUtils.copyProperties(lcDicCompanyPage, lcDicCompany);
		//先从redis 中获取公司的当前编码
		//如果没取到，取数据库查询，并放到redis中。
		//如果取到了直接使用。
		//生成新的编码后放置到redis中
		//所有操作，设置失效时间。

		//先从redis 中获取公司的当前编码
		String CyCode = (redisUtil.get(RedisConstants.DICT_CYCODE) == null) ? "" : redisUtil.get(RedisConstants.DICT_CYCODE).toString();
		int max = 1000;
		int min = 100;
		Random random = new Random();
		if(StringUtils.isEmpty(CyCode)){
			//如果没取到，取数据库查询，并放到redis中。
			CyCode = YouBianCodeUtil.getNextYouBianCode(lcDicCompanyService.queryLastCode());
		}else{
			//如果取到了直接使用。
			CyCode = YouBianCodeUtil.getNextYouBianCode(CyCode);
		}
		redisUtil.set(RedisConstants.DICT_CYCODE,CyCode,random.nextInt(max)%(max-min+1) + min);
		lcDicCompany.setCyCode(CyCode);
		lcDicCompanyService.saveMain(lcDicCompany, lcDicCompanyPage.getLcDicBankCardList());
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param lcDicCompanyPage
	 * @return
	 */
	@AutoLog(value = "公司管理-编辑")
	@ApiOperation(value="公司管理-编辑", notes="公司管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicCompanyPage lcDicCompanyPage) {
		LcDicCompany lcDicCompany = new LcDicCompany();
		BeanUtils.copyProperties(lcDicCompanyPage, lcDicCompany);
		LcDicCompany lcDicCompanyEntity = lcDicCompanyService.getById(lcDicCompany.getId());
		if(lcDicCompanyEntity==null) {
			return Result.error("未找到对应数据");
		}
		lcDicCompanyService.updateMain(lcDicCompany, lcDicCompanyPage.getLcDicBankCardList());
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "公司管理-通过id删除")
	@ApiOperation(value="公司管理-通过id删除", notes="公司管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
		lcDicCompanyService.delMain(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "公司管理-批量删除")
	@ApiOperation(value="公司管理-批量删除", notes="公司管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lcDicCompanyService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "公司管理-通过id查询")
	@ApiOperation(value="公司管理-通过id查询", notes="公司管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Integer id) {
		LcDicCompany lcDicCompany = lcDicCompanyService.getById(id);
		if(lcDicCompany==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicCompany);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "银行卡号管理通过主表ID查询")
	@ApiOperation(value="银行卡号管理主表ID查询", notes="银行卡号管理-通主表ID查询")
	@GetMapping(value = "/queryLcDicBankCardByMainId")
	public Result<?> queryLcDicBankCardListByMainId(@RequestParam(name="id",required=true) Integer id) {
		List<LcDicBankCard> lcDicBankCardList = lcDicBankCardService.selectByMainId(id);
		return Result.ok(lcDicBankCardList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lcDicCompany
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LcDicCompany lcDicCompany) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<LcDicCompany> queryWrapper = QueryGenerator.initQueryWrapper(lcDicCompany, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<LcDicCompany> queryList = lcDicCompanyService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<LcDicCompany> lcDicCompanyList = new ArrayList<LcDicCompany>();
      if(oConvertUtils.isEmpty(selections)) {
          lcDicCompanyList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          lcDicCompanyList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<LcDicCompanyPage> pageList = new ArrayList<LcDicCompanyPage>();
      for (LcDicCompany main : lcDicCompanyList) {
          LcDicCompanyPage vo = new LcDicCompanyPage();
          BeanUtils.copyProperties(main, vo);
          List<LcDicBankCard> lcDicBankCardList = lcDicBankCardService.selectByMainId(main.getId());
          vo.setLcDicBankCardList(lcDicBankCardList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "公司管理列表");
      mv.addObject(NormalExcelConstants.CLASS, LcDicCompanyPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("公司管理数据", "导出人:"+sysUser.getRealname(), "公司管理"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
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
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<LcDicCompanyPage> list = ExcelImportUtil.importExcel(file.getInputStream(), LcDicCompanyPage.class, params);
              for (LcDicCompanyPage page : list) {
                  LcDicCompany po = new LcDicCompany();
                  BeanUtils.copyProperties(page, po);
                  lcDicCompanyService.saveMain(po, page.getLcDicBankCardList());
              }
              return Result.ok("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
    }

}
