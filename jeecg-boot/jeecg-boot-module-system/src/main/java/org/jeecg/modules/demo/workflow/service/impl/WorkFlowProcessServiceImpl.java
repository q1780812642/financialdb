package org.jeecg.modules.demo.workflow.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.workflow.service.IWorkFlowProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class WorkFlowProcessServiceImpl implements IWorkFlowProcessService {


    //流程图相关
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    /**
     * @Description: Flowable流程引擎
     */
    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;




    @Override
    @Transactional(rollbackFor=Exception.class)//扩大事务回滚范围
    public void completeTask(JSONObject jsonObject) {
        //获取参数
        String opinion = jsonObject.getString("opinion");//审批意见
        String outcome = jsonObject.getString("outcome");//连线的分支--一般1表示通过，2-表示驳回
        String taskId = jsonObject.getString("taskId");//任务ID

        //获取当前登录人
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取当前登录人

        Map<String,Object> map =  jsonObject.getInnerMap();
        //map.put("money","1000");
        //map.put("opinion",opinion);
        //map.put("outcome",outcome);
        if(sysUser!=null){
            map.put("assigneeUserId",sysUser.getId());
        }


        taskService.addComment(taskId, null, opinion).setUserId(jsonObject.getString("userId"));//添加批注意见
        taskService.setVariablesLocal(taskId,map);//存放参数
        // d.提交任务
        taskService.complete(taskId,map);

        // a.任务申领
        //taskService.claim(taskId, "e9ca23d68d884d4ebb19d07889727dae");
        // b.添加批注意见信息
        //List<Comment> taskComments = taskService.getTaskComments("taskId");
        //return null;
        //taskService.complete(taskId, map);
        //


    }
}
