<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属公司">
              <j-dict-select-tag v-model="queryParam.companyId"  placeholder="请选择所属公司" dictCode="lc_dic_company,cy_name,id"/>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="账户名称">
              <a-input placeholder="请输入账户名称" v-model="queryParam.accountName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="账号">
              <a-input placeholder="请输入账号" v-model="queryParam.cardNo"></a-input>
            </a-form-item>
          </a-col>
           <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="开户行">
              <a-input placeholder="请输入开户行" v-model="queryParam.openBank"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('银行卡号管理')">导出</a-button>
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

    <lcDicBankCard-modal ref="modalForm" @ok="modalFormOk"></lcDicBankCard-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import LcDicBankCardModal from './modules/LcDicBankCardModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import {getAction} from '@/api/manage'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'

  export default {
    name: "LcDicBankCardList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      LcDicBankCardModal,
      JDictSelectTag,
    },
    data () {
      return {
        description: '银行卡号管理管理页面',
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
            title:'所属公司',
            align:"center",
            dataIndex: 'companyId_dictText'
          },
          {
            title:'账户名称',
            align:"center",
            dataIndex: 'accountName'
          },
          {
            title:'账号',
            align:"center",
            dataIndex: 'cardNo'
          },
          {
            title:'开户行',
            align:"center",
            dataIndex: 'openBank'
          },
          {
            title:'联行号',
            align:"center",
            dataIndex: 'unionpayNo'
          },
          {
            title:'开户行省份',
            align:"center",
            dataIndex: 'openProvince'
          },
          {
            title:'开户行城市',
            align:"center",
            dataIndex: 'openCity'
          },
          {
            title:'类型',
            align:"center",
            dataIndex: 'types_dictText'
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
          list: "/dictionary/lcDicBankCard/list",
          delete: "/dictionary/lcDicBankCard/delete",
          deleteBatch: "/dictionary/lcDicBankCard/deleteBatch",
          exportXlsUrl: "/dictionary/lcDicBankCard/exportXls",
          importExcelUrl: "dictionary/lcDicBankCard/importExcel",
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
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>