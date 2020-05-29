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

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.common.BaseDataConstants;
import org.jeecg.modules.demo.dictionary.entity.LcDicBankCard;
import org.jeecg.modules.demo.dictionary.service.ILcDicBankCardService;

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
 * @Description: 银行卡号管理
 * @Author: jeecg-boot
 * @Date:   2020-05-27
 * @Version: V1.0
 */
@Api(tags="银行卡号管理")
@RestController
@RequestMapping("/dictionary/lcDicBankCard")
@Slf4j
public class LcDicBankCardController extends JeecgController<LcDicBankCard, ILcDicBankCardService> {
	@Autowired
	private ILcDicBankCardService lcDicBankCardService;
	
	/**
	 * 分页列表查询
	 *
	 * @param lcDicBankCard
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-分页列表查询")
	@ApiOperation(value="银行卡号管理-分页列表查询", notes="银行卡号管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcDicBankCard lcDicBankCard,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<LcDicBankCard> queryWrapper = QueryGenerator.initQueryWrapper(lcDicBankCard, req.getParameterMap());
		QueryWrapper<LcDicBankCard> queryWrapper = new QueryWrapper<>();
		Page<LcDicBankCard> page = new Page<LcDicBankCard>(pageNo, pageSize);
		queryWrapper.eq("del_flag", BaseDataConstants.DeleteFlag.DELETE.getValue());
		if(lcDicBankCard.getCompanyId() != null){
			queryWrapper.eq("company_id",lcDicBankCard.getCompanyId());//所属公司
		}
		queryWrapper.like(StringUtils.isNotBlank(lcDicBankCard.getAccountName()),"account_name",lcDicBankCard.getAccountName());//账户名称
		queryWrapper.like(StringUtils.isNotBlank(lcDicBankCard.getCardNo()),"card_no",lcDicBankCard.getCardNo());//账户
		queryWrapper.like(StringUtils.isNotBlank(lcDicBankCard.getOpenBank()),"open_bank",lcDicBankCard.getOpenBank());//开户行
		queryWrapper.like(StringUtils.isNotBlank(lcDicBankCard.getUnionpayNo()),"unionpay_no",lcDicBankCard.getUnionpayNo());//联行号
		IPage<LcDicBankCard> pageList = lcDicBankCardService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param lcDicBankCard
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-添加")
	@ApiOperation(value="银行卡号管理-添加", notes="银行卡号管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcDicBankCard lcDicBankCard) {
		lcDicBankCardService.save(lcDicBankCard);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param lcDicBankCard
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-编辑")
	@ApiOperation(value="银行卡号管理-编辑", notes="银行卡号管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcDicBankCard lcDicBankCard) {
		lcDicBankCardService.updateById(lcDicBankCard);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-通过id删除")
	@ApiOperation(value="银行卡号管理-通过id删除", notes="银行卡号管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
		LcDicBankCard lcDicBankCard =  lcDicBankCardService.getById(id);
		lcDicBankCard.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
		lcDicBankCardService.updateById(lcDicBankCard);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-批量删除")
	@ApiOperation(value="银行卡号管理-批量删除", notes="银行卡号管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		for(String id : ids.split(",")){
			LcDicBankCard lcDicBankCard =  lcDicBankCardService.getById(id);
			lcDicBankCard.setDelFlag(BaseDataConstants.DeleteFlag.NODELETE.getValue());
			lcDicBankCardService.updateById(lcDicBankCard);
		}
		// this.lcDicBankCardService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "银行卡号管理-通过id查询")
	@ApiOperation(value="银行卡号管理-通过id查询", notes="银行卡号管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Integer id) {
		LcDicBankCard lcDicBankCard = lcDicBankCardService.getById(id);
		if(lcDicBankCard==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcDicBankCard);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lcDicBankCard
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LcDicBankCard lcDicBankCard) {
        return super.exportXls(request, lcDicBankCard, LcDicBankCard.class, "银行卡号管理");
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
        return super.importExcel(request, response, LcDicBankCard.class);
    }

}
