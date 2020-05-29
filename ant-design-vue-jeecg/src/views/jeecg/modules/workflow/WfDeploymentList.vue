<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
              <a-col :xl="6" :lg="7" :md="8" :sm="24">
                <a-form-item label="流程ID">
                  <j-input placeholder="请输入流程ID" v-model="queryParam.pbId"></j-input>
                </a-form-item>
              </a-col>
               <a-col :xl="6" :lg="7" :md="8" :sm="24">
                <a-form-item label="流程名称">
                  <j-input placeholder="请输入流程名称" v-model="queryParam.pbName"></j-input>
                </a-form-item>
              </a-col>
               <a-col :xl="6" :lg="7" :md="8" :sm="24">
                <a-form-item label="流程Key">
                  <j-input placeholder="请输入流程Key" v-model="queryParam.pbKey"></j-input>
                </a-form-item>
              </a-col>
              

              <!-- 查询按钮-->
               <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-col :xl="6" :lg="7" :md="8" :sm="24">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                  <!-- <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/> 
                  </a> -->
                </a-col>
              </span>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('请假流程')">导出</a-button> -->
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportBpmn">
        <a-button type="primary" icon="import">导入流程图</a-button>
      </a-upload>
      
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          
           <a @click="getViewDeployImg(record)">流程图 </a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <WfDeploymentModel ref="modalForm" @ok="modalFormOk"></WfDeploymentModel>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WfDeploymentModel from './WfDeploymentModel'
  import JInput from '@/components/jeecg/JInput'


  export default {
    name: "WfDeploymentList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      WfDeploymentModel,
      JInput
    },
    data () {
      return {
        description: '流程部署管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
         {
            title:'流程ID',
            align:"center",
            dataIndex: 'pbId'
            
          },
          {
            title:'流程名称',
            align:"center",
            dataIndex: 'pbName'
            
          },
          {
            title:'流程Key',
            align:"center",
            dataIndex: 'pbKey'
          },
          {
            title:'版本号',
            align:"center",
            dataIndex: 'pbVersion'
          },
          {
            title:'类别',
            align:"center",
            dataIndex: 'pbCategory'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            // fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/workflow/workFlowProcess/deploymentList",
          delete: "/workflow/lcWfAskLeave/delete",
          deleteBatch: "/workflow/lcWfAskLeave/deleteBatch",
          exportXlsUrl: "/workflow/lcWfAskLeave/exportXls",
          importExcelUrl: "/workflow/workFlowProcess/uploadFileBpmn",
        },
        dictOptions:{},
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      
      getViewDeployImg(record){
          console.log(record);
      },
       /* 上传流程图 */
    handleImportBpmn(info){
      console.log("------这是上传后的返回info"+info);
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (info.file.status === 'done') {
        if (info.file.response.success) {
          
          this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`)
          loadData();
        } else {
          this.$message.error(`${info.file.name} ${info.file.response.message}.`);
        }
      } else if (info.file.status === 'error') {
        this.$message.error(`文件上传失败: ${info.file.msg} `);
      }
    },

     

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>