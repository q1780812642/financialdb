<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <div class="table-page-search-wrapper">
      <!-- 搜索区域 -->
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
           
          <a-col :md="6" :sm="8">
            <a-form-item label="项目编码" :labelCol="{span: 8}" :wrapperCol="{span: 8, offset: 1}">
              <a-input placeholder="" v-model="queryParam.itemCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="项目名称" :labelCol="{span: 5}" :wrapperCol="{span: 8, offset: 1}">
              <a-input placeholder="" v-model="queryParam.itemName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="部门名称" :labelCol="{span: 5}" :wrapperCol="{span: 8, offset: 1}">
              <a-input placeholder="" v-model="queryParam.departName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="日期范围" :labelCol="{span: 5}" :wrapperCol="{span: 8, offset: 1}">
              <a-locale-provider :locale="locale">
                <a-range-picker
                  @change="onChange"
                  @calendarChange="calendarChange"
                  :ranges="ranges"
                  :disabledDate="disabledDate"
                />
              </a-locale-provider>
            </a-form-item>
          </a-col>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="12" :sm="24">
                <a-button type="primary" @click="searchQuery" icon="search" style="margin-left: 21px">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('项目管理')">导出</a-button>
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

    <lcDicItem-modal ref="modalForm" @ok="modalFormOk"></lcDicItem-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { formatDate } from '@/utils/util'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import LcDicItemModal from './modules/LcDicItemModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import JDate from '@/components/jeecg/JDate'
  import zhCN from "ant-design-vue/lib/locale-provider/zh_CN";
  import moment from "moment";


  export default {
    name: "LcDicItemList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      LcDicItemModal,
      JDate,
    },
    data () {
      return {
        description: '项目管理管理页面',
        locale: zhCN,
         ranges: {
        一月内: () => [
          moment("2020-10-10", "YYYY:MM:DD"),
          moment("2020-11-10", "YYYY:MM:DD")
        ],
        昨天: () => [
          moment()
            .startOf("day")
            .subtract(1, "days"),
          moment()
            .endOf("day")
            .subtract(1, "days")
        ],
        最近一周: () => [
          moment()
            .startOf("day")
            .subtract(1, "weeks"),
          moment()
        ]},
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
            title:'所属部门',
            align:"center",
            dataIndex: 'departName'
          },
          {
            title:'项目编码',
            align:"center",
            dataIndex: 'itemCode'
          },
          {
            title:'项目名称',
            align:"center",
            dataIndex: 'itemName'
          },
          {
            title:'开始日期',
            align:"center",
            dataIndex: 'startDate',
            customRender:function (text) {
              return formatDate(text,'yyyy-MM-dd');
            }
          },
          {
            title:'结束日期',
            align:"center",
            dataIndex: 'endDate',
            customRender:function (text) {
              return formatDate(text,'yyyy-MM-dd');
            }
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
          list: "/dictionary/lcDicItem/list",
          delete: "/dictionary/lcDicItem/delete",
          deleteBatch: "/dictionary/lcDicItem/deleteBatch",
          exportXlsUrl: "/dictionary/lcDicItem/exportXls",
          importExcelUrl: "dictionary/lcDicItem/importExcel",
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
       moment,
      //不可选日期
      disabledDate(current) {
        // if (this.selectDate != null) {
        //   return current < this.selectDate;
        // } else {
        //   return current < moment().endOf("day");
        // }
      },
      //选中日期事件
      calendarChange(date, dateString) {
        // if (date.length <= 1) {
        //   //选中开始日期事件，设置结束日期必须大于5天
        //   this.selectDate = date[0].add(5, "days");
        // } else {
        //   this.selectDate = null;
        // }
      },
      //选择后的事件
      onChange(date, dateString) {
        this.queryParam.startDate = dateString[0]
        this.queryParam.endDate = dateString[1]
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>