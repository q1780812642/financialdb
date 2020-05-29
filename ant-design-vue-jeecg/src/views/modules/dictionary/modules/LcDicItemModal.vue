<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="项目编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['itemCode', validatorRules.itemCode]" placeholder="自动生成项目编码" disabled></a-input>
        </a-form-item>
        <a-form-item label="项目名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['itemName', validatorRules.itemName]" placeholder="请输入项目名称"></a-input>
        </a-form-item>
        <a-form-item label="开始日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择开始日期" v-decorator="['startDate', validatorRules.startDate]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="结束日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择结束日期" v-decorator="['endDate', validatorRules.endDate]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <!-- <a-form-item label="关联部门" :labelCol="labelCol" :wrapperCol="wrapperCol"> -->
        <a-form-item label="部门分配" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!departDisabled">
          <a-input-search
              v-model="departName"
              placeholder="点击选择部门"
              readOnly
              @search="onSearch">
              <a-button slot="enterButton" icon="search">选择</a-button>
            </a-input-search>
         </a-form-item>

        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea placeholder="请输入备注" v-decorator="['notes']" />
        </a-form-item>

      </a-form>
    </a-spin>
    
    <depart-window ref="departWindow" @ok="modalFormOk"></depart-window>

  </j-modal>
</template>

<script>

  import DepartWindow from '@/views/system/modules/DepartWindow'
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'  

  export default {
    name: "LcDicItemModal",
    components: { 
      JDate,
      DepartWindow,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
          itemName: {
            rules: [
              { required: true, message: '请输入项目名称!'},
            ]
          },
          startDate: {
            rules: [
              { required: true, message: '请输入开始日期!'},
              { validator: this.selectStartDate},
            ]
          },
          endDate: {
            rules: [
              { required: true, message: '请输入结束日期!'},
              { validator: this.selectEndDate},
            ]
          },
        },
        url: {
          add: "/dictionary/lcDicItem/add",
          edit: "/dictionary/lcDicItem/edit",
        },
        departDisabled: false, //是否是我的部门调用该页面
        departId: '',
        departName: '',
      }
    },
    created () {
    },
    methods: {
      // 搜索用户对应的部门API
      onSearch(){
        this.$refs.departWindow.add(this.checkedDepartKeys,this.userId);
      },
      // 获取用户对应部门弹出框提交给返回的数据
      modalFormOk (formData) {
        this.departName = formData.departIdList[0].title
        let a = {"departId" : formData.departIdList[0].key,
                "departName" : formData.departIdList[0].title};
        this.form.setFieldsValue(pick(a,"departId"));
       },
      // 根据屏幕变化,设置抽屉尺寸
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'itemCode','itemName','startDate','endDate','states','notes','delFlag','departId','createBy','updateTime','createTime','sysOrgCode','updateBy','izEnable'))
        })
        this.departName = this.model.departName
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.departName = '';
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'itemCode','itemName','startDate','endDate','states','notes','delFlag','departId','createBy','updateTime','createTime','sysOrgCode','updateBy','izEnable'))
      },
      //选择开始日期
      selectStartDate(rule, val, callback){
        let formData = this.form.getFieldsValue()
        let flag = true
        if(formData.endDate != null && formData.endDate != undefined && formData.endDate != ''){
          if(val >= formData.endDate){
            flag = false
          }
        }
        if (!flag) {
            callback('开始日期不可大于或等于结束日期！');
        }
        callback();
      },
      //选择结束日期
      selectEndDate(rule, val, callback){
        let formData = this.form.getFieldsValue()
        let flag = true
        if(formData.startDate != null && formData.startDate != undefined && formData.startDate != ''){
          if(val <= formData.startDate){
            flag = false
          }
        }
        if (!flag) {
            callback('结束日期不可小于或等于开始日期！');
        }
        callback();
      }
    }
  }
</script>