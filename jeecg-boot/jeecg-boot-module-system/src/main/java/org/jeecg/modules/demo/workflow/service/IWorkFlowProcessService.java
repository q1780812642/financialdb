package org.jeecg.modules.demo.workflow.service;

import com.alibaba.fastjson.JSONObject;

public interface IWorkFlowProcessService {


    /**
     * 提交审批任务
     * @param jsonObject
     */
    void completeTask(JSONObject jsonObject);

}
