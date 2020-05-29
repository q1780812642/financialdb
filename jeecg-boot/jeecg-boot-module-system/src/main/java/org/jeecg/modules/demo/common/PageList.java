package org.jeecg.modules.demo.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.jeecg.common.api.vo.Result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PageList {

    /**
     * 功能描述: <br>
     * 〈对list数据集进行分页处理，返回IPage集合〉
     * @date 2020/5/25 14:26
     * @Author: CuiHonglei
     * @Param: [pageNo, pageSize, list]
     * @Return: com.baomidou.mybatisplus.core.metadata.IPage<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static IPage<Map<String, Object>> paging(Integer pageNo,Integer pageSize,Long pdsCount,List<?> pdsList){

        //把结果集封装到mapList
        List<Map<String, Object>>  mapList = activitiResult(pdsList);
        //组装模型-返回前台
        IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
        pageList.setRecords(mapList);
        pageList.setTotal(pdsCount);
        pageList.setPages(pageNo);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }


    /**
     * 把 实体对象  转换为 map
     */
    private static List<Map<String, Object>> activitiResult(List<?> objs) {
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
                        pdKey = methodName.substring(3);
                        // 将首字母改为小写
                        pdKey = pdKey.substring(0,1).toLowerCase() + pdKey.substring(1,pdKey.length());
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

}
