package org.jeecg.modules.demo.workflow.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.workflow.entity.LcWfAskLeave;
import org.jeecg.modules.demo.workflow.service.ILcWfAskLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 请假流程
 * @Author: jeecg-boot
 * @Date:   2020-05-08
 * @Version: V1.0
 */
@Api(tags="请假流程")
@RestController
@RequestMapping("/workflow/lcWfAskLeave")
@Slf4j
public class LcWfAskLeaveController extends JeecgController<LcWfAskLeave, ILcWfAskLeaveService> {
	@Autowired
	private ILcWfAskLeaveService lcWfAskLeaveService;
	 @Autowired
	 private RuntimeService runtimeService;
	 @Autowired
	 private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	/**
	 * @Description: Flowable流程引擎
	 */
	@Qualifier("processEngine")
	@Autowired
	private ProcessEngine processEngine;
	/**
	 * @Description:
	 */
	@Autowired
	private HistoryService historyService;

	/**
	 * flowable 工具类
	 */
	@Autowired
	private FlowUtil flowUtil;





	 /**工作流业务 END*/

	/**
	 * 分页列表查询
	 *
	 * @param lcWfAskLeave
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "请假流程-分页列表查询")
	@ApiOperation(value="请假流程-分页列表查询", notes="请假流程-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LcWfAskLeave lcWfAskLeave,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LcWfAskLeave> queryWrapper = QueryGenerator.initQueryWrapper(lcWfAskLeave, req.getParameterMap());
		Page<LcWfAskLeave> page = new Page<LcWfAskLeave>(pageNo, pageSize);
		IPage<LcWfAskLeave> pageList = lcWfAskLeaveService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param lcWfAskLeave
	 * @return
	 */
	@AutoLog(value = "请假流程-添加")
	@ApiOperation(value="请假流程-添加", notes="请假流程-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LcWfAskLeave lcWfAskLeave) {
		lcWfAskLeaveService.save(lcWfAskLeave);

		/*Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("E:\\sdlcIdea\\jeecg-boot\\jeecg-boot\\jeecg-boot-module-system\\src\\main\\resources\\processes\\baoxiao.bpmn20.xml")
				.deploy();*/

		//LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取当前登录人
		String[] abc = {"张三11","李四22","王五33","赵六44"};

		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("taskUser", "e9ca23d68d884d4ebb19d07889727dae");//任务发起人
		//map.put("procdefNum", lcWfAskLeave.getSqDescribe());//流程编号
		map.put("assessList", Arrays.asList(abc));//流程编号

		String procdefKey = lcWfAskLeave.getSqDescribe();
		//String businessId = "测试报销流程【"+sysUser.getRealname()+"】";
		//String userId = sysUser.getId();

		String businessId = "测试会签1";
		String userId = "e9ca23d68d884d4ebb19d07889727dae";
		String insId = flowUtil.startPrecessIns(procdefKey,businessId,userId,map);


		return Result.ok("提交成功.流程Id为：" + insId);


		//return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param lcWfAskLeave
	 * @return
	 */
	@AutoLog(value = "请假流程-编辑")
	@ApiOperation(value="请假流程-编辑", notes="请假流程-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LcWfAskLeave lcWfAskLeave) {
		lcWfAskLeaveService.updateById(lcWfAskLeave);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "请假流程-通过id删除")
	@ApiOperation(value="请假流程-通过id删除", notes="请假流程-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		lcWfAskLeaveService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "请假流程-批量删除")
	@ApiOperation(value="请假流程-批量删除", notes="请假流程-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lcWfAskLeaveService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "请假流程-通过id查询")
	@ApiOperation(value="请假流程-通过id查询", notes="请假流程-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LcWfAskLeave lcWfAskLeave = lcWfAskLeaveService.getById(id);
		if(lcWfAskLeave==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(lcWfAskLeave);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lcWfAskLeave
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LcWfAskLeave lcWfAskLeave) {
        return super.exportXls(request, lcWfAskLeave, LcWfAskLeave.class, "请假流程");
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
        return super.importExcel(request, response, LcWfAskLeave.class);
    }

}
