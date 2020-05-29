package org.jeecg.modules.demo.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.workflow.service.IWorkFlowProcessService;
import org.jeecg.modules.demo.workflow.service.impl.CustomProcessDiagramGenerator;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 工作流通用controller
 * @Author: zzx
 * @Date:   2020-05-08
 * @Version: V1.0
 */
@Api(tags="工作流常用")
@RestController
@RequestMapping("/workflow/workFlowProcess")
@Slf4j
public class WorkFlowProcessController  {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ISysLogService logService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ISysUserService sysUserService;
	//流程图相关
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private CustomProcessDiagramGenerator customProcessDiagramGenerator;
	/**
	 * @Description: 获取Flowable服务
	 */
	@Autowired
	private RepositoryService repositoryService;
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

	@Autowired
	private IWorkFlowProcessService workFlowProcessService;


	private  Color COLOR_NORMAL = new Color(0, 205, 0);
	//红色
	private  Color COLOR_CURRENT = new Color(255, 0, 0);

	/**
	 * 上传流程图
	 * @param request
	 * @param response
	 * @return
	 */
	//@AutoLog(value = "上传流程图")
	@RequestMapping(value = "/uploadFileBpmn", method = RequestMethod.POST)
	public Result<?> uploadFileBpmn(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

		String uploadPath = "processes";// 上传文件夹
		String fileName = null;//存放文件名称
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			// 获得原始文件名
			fileName = file.getOriginalFilename();
			try {
				// 拼接上传文件位置
				String lad = this.getClass().getClassLoader().getResource("").getPath().toString();
				System.out.println(lad);
				String newfilePath = uploadPath + "\\" + fileName;
				// 创建文件实例
				File uploadFile = new File(newfilePath);
				// 判断文件已经存在，则删除该文件
				if (uploadFile.exists()) {
					uploadFile.delete();
				}
				// FileUtils.copyInputStreamToFile()将文件复制
				FileUtils.copyInputStreamToFile(file.getInputStream(), uploadFile);

				// 流程图入库
				//repositoryService.createDeployment().category("测试类型").addBytes(newfilePath, file.getBytes()).deploy().getEngineVersion(); //设置类型
				//repositoryService.createDeployment().addBytes(newfilePath, file.getBytes()).deploy().getEngineVersion();
				repositoryService.createDeployment().addInputStream(newfilePath, file.getInputStream()).deploy().getEngineVersion();
			} catch (IOException e) {
				e.printStackTrace();
				Result.error(e.getMessage());
			}

		}
		return Result.ok("流程图上传成功");
	}


	/**
	 * 查询流程部署列表
	 * @param pageNo
	 * @param pageSize
	 * @param pbId
	 * @param pbName
	 * @param pbKey
	 * @param req
	 * @return
	 */
	@GetMapping("/deploymentList")
	@ApiOperation(value = "查询流程部署列表", notes = "查询流程部署列表")
	public Result<?> deploymentList( @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									 @RequestParam(name="pbId",required=false) String pbId,@RequestParam(name="pbName",required=false) String pbName,
									 @RequestParam(name="pbKey",required=false) String pbKey,
									 HttpServletRequest req) {
		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页
		List<ProcessDefinition> pdsList = null;
		Long pdsCount = 0L;//存放总条数
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		//动态组装查询SQL
		if(StringUtils.isNotEmpty(pbId)){
			query = query.processDefinitionId(pbId);
		}
		if(StringUtils.isNotEmpty(pbName)){
			pbName = pbName.replace("*","%");
			query.processDefinitionNameLike(pbName);
		}
		if(StringUtils.isNotEmpty(pbKey)){
			pbKey = pbKey.replace("*","%");
			query.processDefinitionKeyLike(pbKey);
		}
		ProcessDefinitionQuery countQuery = query.latestVersion();

		pdsCount = countQuery.count();
		pdsList = query.listPage(firstResult, maxResults);

		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(pdsList);

		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < pdsList.size(); i++) {
			mapList.get(i).put("pbId",pdsList.get(i).getId());
		}

		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(pdsCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		pdsList.clear();

		return Result.ok(pageList);
	}



	/**
	 * @Description: 待办任务查询
	 * //@param title 项目名称查询
	 * //@param workflowName 流程类别
	 * @return 操作结果
	 */
	@GetMapping("/todoTasksList")
	@ApiOperation(value = "待办任务查询", notes = "待办任务查询")
	public Result<?> todoTasksList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									 @RequestParam(name="businessKey", required = false) String businessKey,
									 @RequestParam(name="pbProcessDefinitionId", required = false) String pbProcessDefinitionId) {
		//获取分页
		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		//获取当前登录人
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取当前登录人

		String userId = "e9ca23d68d884d4ebb19d07889727dae";
		//String userId = sysUser.getId();
				// b.查询候选人的任务
		Long listCount = 0L;
		List<Task> tasksList = null;

		TaskQuery query = taskService.createTaskQuery();


		query = query.taskCandidateOrAssigned(userId);//候选人 or 指派人是当前登录人员

		//动态组装SQL
		if(StringUtils.isNotEmpty(businessKey)){
			//业务标题
			businessKey = businessKey.replace("*","%");
			query.processInstanceBusinessKeyLike(businessKey);
		}

		if(StringUtils.isNotEmpty(pbProcessDefinitionId)){
			//流程KEY
			pbProcessDefinitionId = pbProcessDefinitionId.replace("*","%");
			query.processDefinitionKeyLike(pbProcessDefinitionId);
		}



		listCount = query.count();
		tasksList = query.orderByTaskCreateTime().desc().listPage(firstResult, maxResults);

		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(tasksList);

		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < tasksList.size(); i++) {
			mapList.get(i).put("pbId",tasksList.get(i).getId());

			String processInstanceId = tasksList.get(i).getProcessInstanceId();
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			mapList.get(i).put("businessKey",processInstance.getBusinessKey());
			mapList.get(i).put("definitionName",processInstance.getProcessDefinitionName());//流程名称

			SysUser byId = sysUserService.getById(processInstance.getStartUserId());
			if(byId!=null){
				mapList.get(i).put("applyRealName",byId.getRealname());//流程发起人
			}


		}
		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(listCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		tasksList.clear();
		return Result.ok(pageList);
	}


	/**
	 * 流程实例-分页列表查询
	 * @param pageNo 页码
	 * @param pageSize 每页条数
	 * @return
	 */
	@GetMapping("/processInstanceList")
	@ApiOperation(value = "查询流程实例", notes = "查询流程实例")
	public Result<?> processInstanceList( @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										  @RequestParam(name="pbProcessDefinitionName",required = false) String pbProcessDefinitionName,
										  @RequestParam(name="pbProcessDefinitionKey",required = false) String pbProcessDefinitionKey) {

		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		List<ProcessInstance> processInsList = null;
		Long pdsCount = 0L;//存放总条数
		ProcessInstanceQuery query =  runtimeService.createProcessInstanceQuery();
		//runtimeService.createNativeProcessInstanceQuery().sql().list();
		//动态组装SQL
		if(StringUtils.isNotEmpty(pbProcessDefinitionName)){
			//流程名称
			//query = query.processDefinitionName();
		}

		pdsCount = query.count();
		// List<HistoricProcessInstance> listProcessInstance =
		//		// historyService.createHistoricProcessInstanceQuery().finished().processDefinitionId("XXX").orderByProcessInstanceDuration().desc().listPage(firstResult,
		//		// maxResults);
		processInsList = query.orderByProcessDefinitionId().desc().listPage(firstResult, maxResults);
		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(processInsList);
		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < processInsList.size(); i++) {
			mapList.get(i).put("pbId",processInsList.get(i).getId());

			//发起人
			SysUser byId = sysUserService.getById(processInsList.get(i).getStartUserId());
			if(byId!=null){
				mapList.get(i).put("applyRealName",byId.getRealname());//流程发起人
			}
		}

		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(pdsCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		processInsList.clear();

		return Result.ok(pageList);
	}

	/**
	 * 历史流程实例查询
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/historicalProcessList")
	@ApiOperation(value = "历史流程实例查询", notes = "历史流程实例查询")
	public Result<?> historicalProcessList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {


		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		List<ProcessInstance> processInsList = null;
		Long pdsCount = 0L;//存放总条数


		Long listCount = 0L;
		List<HistoricProcessInstance> list = null;


		// b.查询已完成的历史任务
		listCount = historyService.createHistoricProcessInstanceQuery().finished().count();
		list = historyService.createHistoricProcessInstanceQuery().finished().orderByProcessInstanceEndTime().desc().listPage(firstResult, maxResults);


		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(list);

		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < list.size(); i++) {
			mapList.get(i).put("pbId",list.get(i).getId());
			HistoricProcessInstance historicProcessInstance = list.get(i);

			SysUser byId = sysUserService.getById(list.get(i).getStartUserId());
			if(byId!=null){
				mapList.get(i).put("applyRealName",byId.getRealname());//流程发起人
			}

			//查询当前节点
			String hisProcessStatu = "处理中";
			//historyService.createHistoricTaskInstanceQuery()
			if(historicProcessInstance.getEndTime()!=null){
				if(StringUtils.isNotEmpty(historicProcessInstance.getDeleteReason())){
					//删除原因不为空
					hisProcessStatu = historicProcessInstance.getDeleteReason();
				}
			}
			mapList.get(i).put("hisProcessStatu",hisProcessStatu);

		}
		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(listCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		list.clear();
		return Result.ok(pageList);
	}


	/**
	 * @Description: 已办任务查询
	 *
	 * @return 操作结果
	 */
	@GetMapping("/finishedTasksList")
	@ApiOperation(value = "已办任务查询", notes = "已办任务查询")
	public Result<?> finishedTasksList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {


		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		List<ProcessInstance> processInsList = null;
		Long pdsCount = 0L;//存放总条数

		//获取当前人员
		//获取当前登录人
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取当前登录人

		String userId = "e9ca23d68d884d4ebb19d07889727dae";
		//String userId = sysUser.getId();
		Long listCount = 0L;
		List<HistoricTaskInstance> list = null;


		// b.查询历史任务
		listCount = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().count();
		list = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().orderByTaskCreateTime().desc().listPage(firstResult, maxResults);


		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(list);

		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < list.size(); i++) {
			mapList.get(i).put("pbId",list.get(i).getId());

			String processInstanceId = list.get(i).getProcessInstanceId();
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			mapList.get(i).put("businessKey",historicProcessInstance.getBusinessKey());
			mapList.get(i).put("definitionName",historicProcessInstance.getProcessDefinitionName());//流程名称

			SysUser byId = sysUserService.getById(historicProcessInstance.getStartUserId());
			if(byId!=null){
				mapList.get(i).put("applyRealName",byId.getRealname());//流程发起人
			}
		}
		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(listCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		list.clear();
		return Result.ok(pageList);
	}


	/**
	 * 我发起的流程实例列表
	 * @param pageNo
	 * @param pageSize
	 * @return 查询结果
	 */
	@GetMapping("/myStartProcintList")
	@ApiOperation(value = "我发起的流程实例列表", notes = "我发起的流程实例列表")
	public Result<?> myStartProcintList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		if(pageNo==1){
			pageNo = 0;
		}
		int firstResult = pageNo * pageSize;//开始页
		int maxResults = firstResult+pageSize;//结束页

		List<HistoricProcessInstance> hisProcessInsList = new ArrayList<HistoricProcessInstance>();
		Long pdsCount = 0L;//存放总条数
		//获取当前登录人
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取当前登录人
		//String userId = sysUser.getId();
		String userId = "e9ca23d68d884d4ebb19d07889727dae";
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
		historicProcessInstanceQuery.startedBy(userId);
		//动态组装SQL

		pdsCount = historicProcessInstanceQuery.count();
		hisProcessInsList = historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc().listPage(firstResult, maxResults);

		//把结果集封装到mapList
		List<Map<String, Object>>  mapList = activitiResult(hisProcessInsList);

		//因反射方法中取不到ID，在此处循环添加进Map
		for (int i = 0; i < hisProcessInsList.size(); i++) {
			HistoricProcessInstance historicProcessInstance = hisProcessInsList.get(i);
			mapList.get(i).put("pbId",historicProcessInstance.getId());

			//查询当前节点
			String hisProcessStatu = "处理中";
			//historyService.createHistoricTaskInstanceQuery()
			if(historicProcessInstance.getEndTime()!=null){
				if(StringUtils.isNotEmpty(historicProcessInstance.getDeleteReason())){
					//删除原因不为空
					hisProcessStatu = historicProcessInstance.getDeleteReason();
				}else{
					hisProcessStatu = "已完成";
				}
			}else{
				//任务在进行中--获取当前节点-当前处理人
				List<Task> taskList = taskService.createTaskQuery().processInstanceId(historicProcessInstance.getId()).list();
				if(taskList!=null && taskList.size()>0){
					mapList.get(i).put("nowTaskName",taskList.get(0).getName());

				}

			}
			mapList.get(i).put("hisProcessStatu",hisProcessStatu);

		}
		//组装模型-返回前台
		IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
		pageList.setRecords(mapList);
		pageList.setTotal(pdsCount);
		pageList.setPages(pageNo);
		pageList.setSize(pageSize);
		pageList.setCurrent(pageNo);

		//清除调用-释放缓存
		hisProcessInsList.clear();
		return Result.ok(pageList);
	}


	/**
	 * 完成任务--测试使用，生产环境进行屏蔽
	 * @return
	 */
	@AutoLog(value = "提交审批任务")
	@ApiOperation(value = "提交审批任务", notes = "提交审批任务")
	@PostMapping("/completeTask")
	public Result<?> completeTask(@RequestBody JSONObject jsonObject) {

		//任务ID为主要参数，不可为空
		if(StringUtils.isEmpty(jsonObject.getString("taskId"))){
			return Result.error("参数错误~");
		}
		try {
			workFlowProcessService.completeTask(jsonObject);
		}catch (Exception e){
			e.printStackTrace();
			return Result.error("提交任务失败~");
		}

		return Result.ok("提交任务成功");
	}


	/**
	 * 指派任务
	 * @param taskId 任务ID
	 * @param userId 指派人
	 * @return
	 */
	@AutoLog(value = "指派任务")
	@ApiOperation(value = "指派任务", notes = "指派任务")
	@PostMapping("/setAssigneeTask")
	public Result<?> setAssigneeTask(@RequestParam(name="taskId") String  taskId,
									 @RequestParam(name="userId") String  userId) {

		taskService.setAssignee(taskId, userId);

		return Result.ok();
	}

	/**
	 * @Author zzx
	 * @Description  //根据流程实例ID，获取全部变量
	 * @Date  2020/5/17
	 * @Param  * @param processId 实例ID
	 * @return {@link Result<?>}
	 **/
	@AutoLog(value = "根据实例ID获取变量")
	@ApiOperation(value = "根据实例ID获取变量", notes = "根据实例ID获取变量")
	@GetMapping("/getHisVariable")
	public Result<?> getHisVariable(@RequestParam(name="processId") String  processId) {

		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().
				processInstanceId(processId).list();
		for (HistoricVariableInstance hisVar: list) {
			System.out.println(hisVar.getVariableName());
			System.out.println(hisVar.getValue());
		}

		return Result.ok();
	}


	/**
	 * @Description: 挂起流程实例
	 *
	 * @Param: processId 当前流程实例id
	 * @return 无
	 **/
	@AutoLog(value = "挂起流程实例")
	@PostMapping("/handUpProcessInstance")
	@ApiOperation(value = "挂起流程实例", notes = " 挂起流程实例")
	public Result<?> handUpProcessInstance(@RequestParam(name="processId") String  processId, HttpServletRequest req) {
		runtimeService.suspendProcessInstanceById(processId);

		return Result.ok("流程挂起成功");
	}

	/**
	 * @Description:恢复（唤醒）被挂起的流程实例
	 *
	 * @Param: processInstanceId 流程实例id
	 * @return: 无
	 **/

	@AutoLog(value = "恢复（唤醒）被挂起的流程实例")
	@PostMapping("/activateProcessInstance")
	@ApiOperation(value = "恢复（唤醒）被挂起的流程实例", notes = "恢复（唤醒）被挂起的流程实例")
	public Result<?> activateProcessInstance(@RequestParam(name="processId") String  processId, HttpServletRequest req) {

		//提前终止流程
		runtimeService.deleteProcessInstance(processId, "流程撤回，流程终止");
		//runtimeService.activateProcessInstanceById(processId);
		return Result.ok("流程唤醒成功");
	}

	/**
	 * 获取常规流程图
	 * @param processDefinitionId 流程部署ID
	 * @param processDefinitionName 流程图名称
	 * @param req
	 * @return
	 */
	@GetMapping("/getViewDeployImg")
	@ApiOperation(value = "查看常规流程图", notes = "查看常规流程图")
	public Result<?> getViewDeployImg( @RequestParam(name="processDefinitionId") String processDefinitionId,
									   @RequestParam(name="processDefinitionName") String processDefinitionName,
									   HttpServletRequest req) {
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

		//参数列表
		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();
		// 高亮线路id集合
		List<String> highLightedFlows = new ArrayList<String>();


		String imgFile = req.getSession().getServletContext().getRealPath("/")+"workFlowPng\\"+processDefinitionName+".jpg";
		System.out.println(imgFile);
		File file = new File(imgFile);
		//判断文件是否存在，存在删除
		if(file.exists()){
			file.delete();
		}
		try(InputStream imageStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivitis, highLightedFlows,
				"宋体", "宋体", "宋体", null, 1.0D,true);){
			//根据前台判断图片处理逻辑-写入图片地址，还是直接写出流
			FileUtils.copyInputStreamToFile(imageStream,file);
		}catch (Exception e){
			e.printStackTrace();
		}

		return Result.ok();
	}


	/**
	 * @Description: 读取动态流程图
	 * @Description: 方法来源：https://www.jianshu.com/p/3f976a47114c
	 * @param processInstanceId 流程实例ID
	 */
	@GetMapping("/readProcessImg")
	@ApiOperation(value = "查看高亮动态流程图", notes = "查看高亮动态流程图")
	public Result<?> readProcessImg(@RequestParam(name="processInstanceId")String processInstanceId,
									@RequestParam(name="processDefinitionName") String processDefinitionName,
									HttpServletRequest req,HttpServletResponse resp) {

		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		// Lambda方式 -》移除线路节点
		highLightedActivitList.removeIf(his -> "sequenceFlow".equals(his.getActivityType()));

		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();
		// 高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		//Lambda方式 --循环添加ID
		highLightedActivitList.forEach(activit -> highLightedActivitis.add(activit.getActivityId()));

		Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list().stream().map(e -> e.getActivityId()).collect(Collectors.toSet());

		//png 极速模式下不兼容
		ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();

		String imgFile = req.getSession().getServletContext().getRealPath("/")+"workFlowPng\\"+processDefinitionName+".jpg";
		System.out.println(imgFile);
		File file = new File(imgFile);
		//判断文件是否存在，存在删除
		if(file.exists()){
			file.delete();
		}
		//try-resource
		try(InputStream imageStream = customProcessDiagramGenerator.generateDiagram(bpmnModel, "jpg",
				highLightedActivitis, highLightedFlows, "宋体", "宋体", "宋体",
				null, 1.0D, new Color[] { COLOR_NORMAL, COLOR_CURRENT }, currIds);){
			//根据前台判断图片处理逻辑-写入图片地址，还是直接写出流
			FileUtils.copyInputStreamToFile(imageStream,file);
		}catch (Exception e){
			e.printStackTrace();
		}

		return Result.ok();
	}

	/**
	 * @Description: 获取需要高亮的线
	 * @Description: 方法来源：https://www.jianshu.com/p/3f976a47114c
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
		List<String> highFlows = new ArrayList<>();// 用以保存高亮的线flowId
		Process process = repositoryService.getBpmnModel(processDefinitionEntity.getId()).getMainProcess();

		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
			List<FlowNode> sameStartTimeNodes = new ArrayList<>();// 用以保存后需开始时间相同的节点
			FlowNode flowNode = (FlowNode) process.getFlowElement(historicActivityInstances.get(i).getActivityId());
			sameStartTimeNodes.add(flowNode);

			//取出下一个节点
			if ((i + 1) <= (historicActivityInstances.size() - 1)) {
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(i + 1);// 后续第二个节点
				FlowNode sameActivityImpl2 = (FlowNode) process.getFlowElement(activityImpl2.getActivityId());
				sameStartTimeNodes.add(sameActivityImpl2);
			}
			List<SequenceFlow> pvmTransitions = flowNode.getOutgoingFlows();// 取出节点的所有出去的线
			// 对所有的线进行遍历
			for (SequenceFlow pvmTransition : pvmTransitions) {
				FlowNode sourcePvmActivityImpl = (FlowNode) pvmTransition.getSourceFlowElement();
				FlowNode targetPvmActivityImpl = (FlowNode) pvmTransition.getTargetFlowElement();
				// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(sourcePvmActivityImpl) && sameStartTimeNodes.contains(targetPvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	/**
	 * 把 实体对象  转换为 map
	 */
	private  List<Map<String, Object>> activitiResult(List<?> objs) {
		// 用于存放多个对象的集合
		List<Map<String, Object>> pdResult = new ArrayList<>();
		// 遍历方法参数中的集合
		for (Object obj : objs) {
			// 用于封装单个对象 get 方法返回值的 Map 集合
			Map<String, Object> pdMap = new HashMap<>();
			Method[] methods = obj.getClass().getDeclaredMethods();
			// 遍历方法对象数组
			for (Method method : methods) {
				// 获取方法名称
				String methodName = method.getName();
				// 判断该方法是否名称不为 null ，并且名称是以 get 开头，满足条件进入 if 中
				if (methodName != null && methodName.startsWith("get")) {
					// 设置方法的访问权限
					method.setAccessible(true);
					String pdKey = "";
					try {

						// 将方法名的 get 前缀去掉，并增加 pd 前缀
						pdKey = "pb".concat(methodName.substring(3));
						// 将 get 方法的名称作为 Map 的 key，将返回值作为 value 进行封装
 						pdMap.put(pdKey, method.invoke(obj, null));
					} catch (Exception e) {
						//e.printStackTrace();
						log.debug("反射获取字段【"+pdKey+"】出现错误");
						continue;
					}
				}
			}
			// 将封装好的 Map 集合添加到 List 集合中
			pdResult.add(pdMap);
		}
		return pdResult;
	}

	public static void main(String[] args) {
		System.out.println("项目调试中");
		System.out.println("这可怎么办哟" +
				"");
		System.out.println("项目调试中222");
	}


}
