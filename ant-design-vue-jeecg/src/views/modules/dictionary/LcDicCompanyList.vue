<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="公司编码">
              <a-input placeholder="请输入公司编码" v-model="queryParam.cyCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="公司名称">
              <a-input placeholder="请输入公司名称" v-model="queryParam.cyName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('公司管理')">导出</a-button>
      <!-- <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
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
        class="j-table-force-nowrap"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
        :customRow="clickThenCheck">

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
          <a @click="handleEdit(record)">编辑</a>

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

    <!-- 银行卡号 -->
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="银行卡号管理" key="1">
        <LcDicBankCardList ref="LcDicBankCardList"></LcDicBankCardList>
      </a-tab-pane>
    </a-tabs>

    <lcDicCompany-modal ref="modalForm" @ok="modalFormOk"></lcDicCompany-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import LcDicCompanyModal from './modules/LcDicCompanyModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'
  import LcDicBankCardList from './LcDicBankCardList'

  export default {
    name: "LcDicCompanyList",
    mixins:[JeecgListMixin],
    components: {
      LcDicCompanyModal,
      LcDicBankCardList
    },
    data () {
      return {
        description: '公司管理管理页面',
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'公司编码',
            align:"center",
            dataIndex: 'cyCode'
          },
          {
            title:'公司名称',
            align:"center",
            dataIndex: 'cyName'
          },
          {
            title:'公司地址',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'公司联系人',
            align:"center",
            dataIndex: 'contact'
          },
          {
            title:'联系人电话',
            align:"center",
            dataIndex: 'phone'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'notes'
          },
          {
            title:'公司税号',
            align:"center",
            dataIndex: 'ein'
          },
          {
            title:'公司类型',
            align:"center",
            dataIndex: 'types_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/dictionary/lcDicCompany/list",
          delete: "/dictionary/lcDicCompany/delete",
          deleteBatch: "/dictionary/lcDicCompany/deleteBatch",
          exportXlsUrl: "/dictionary/lcDicCompany/exportXls",
          importExcelUrl: "dictionary/lcDicCompany/importExcel",
        },
        dictOptions:{},
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){

      },
      //行单机
      clickThenCheck(record) {
       
        return {
          on: {
            click: () => {
              let id = []
              id[0] = record.id
              this.onSelectChange(id, [record]);
            }
          }
        };
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
        this.$refs.LcDicBankCardList.getOrderMain(this.selectedRowKeys[0]);
        
      },
       
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>