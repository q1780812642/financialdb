package org.jeecg.modules.demo.workflow.controller;


import org.apache.commons.lang.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * flowable工作流 工具类
 * @Author Scott
 *
 */
@Component
public class FlowUtil {
    //流程图相关
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    /**
     * @Description: 获取Flowable服务
     */
    @Autowired
    private RepositoryService repositoryService;
    /**
     * @Description: Flowable流程引擎
     */
    @Autowired
    @Qualifier("processEngine")
    private ProcessEngine processEngine;

    /**
     * @Description:
     */
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 启动流程实例
     * 1、根据流程KEY,系统自动查询最新版本流程，进行启动。
     * 2、存放业务ID，启动人ID等字段信息。
     * 3、存放启动参数
     * @param procdefKey 流程部署KEY
     * @param businessId 业务ID
     * @param userId    启动人ID
     * @param map   启动参数
     * @return 流程实例ID
     */
    public String startPrecessIns(String procdefKey, String businessId,String userId, Map<String,Object> map){
        String insId = null;

        if(StringUtils.isNotEmpty(procdefKey)){
            //不为空，进行启动
            if(StringUtils.isNotEmpty(userId)){
                Authentication.setAuthenticatedUserId(userId);
            }
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procdefKey,businessId, map);
            insId = processInstance.getId();
            // 注意一点:设置发起人这个方法最终使用一个ThreadLocal类型的变量进行存储，也就是与当前的线程绑定，所以流程实例启动完毕之后，需要设置为null，防止多线程的时候出问题。
            Authentication.setAuthenticatedUserId(null);
        }
        return insId;
    }


    /**
     * 根据申请人部门、角色编码，动态获取当前候选人
     * @param roleId
     * @return
     */
    public String getCandidateUsers(String roleId) {

        //根据角色编码查询人员
        if (StringUtils.isNotEmpty(roleId)) {
            List<Map<String, Object>> userMapList = sysUserService.queryByDepIdAndRole("", "");
            for (Map<String, Object> map : userMapList) {
                System.out.println(map);
            }
        }

        return roleId;
    }

}
