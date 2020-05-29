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

        <a-form-item label="账户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['accountName', validatorRules.accountName]" placeholder="请输入账户名称"></a-input>
        </a-form-item>
        <a-form-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['cardNo', validatorRules.cardNo]" placeholder="请输入账号"></a-input>
        </a-form-item>
        <a-form-item label="开户行" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['openBank', validatorRules.openBank]" placeholder="请输入开户行"></a-input>
        </a-form-item>
        <a-form-item label="联行号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['unionpayNo', validatorRules.unionpayNo]" placeholder="请输入联行号"></a-input>
        </a-form-item>
        <a-form-item label="开户行省份" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['openProvince']" placeholder="请输入开户行省份"></a-input>
        </a-form-item>
        <a-form-item label="开户行城市" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['openCity']" placeholder="请输入开户行城市"></a-input>
        </a-form-item>
        <a-form-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag v-decorator="['types', validatorRules.types]" :triggerChange="true" placeholder="请选择账户类型" dictCode="bank_type"/>
        </a-form-item>
        <a-form-item label="所属公司" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag v-decorator="['companyId', validatorRules.companyId]" :triggerChange="true" placeholder="请选择所属公司" dictCode="lc_dic_company,cy_name,id"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'


  export default {
    name: "LcDicBankCardModal",
    components: { 
      JDictSelectTag,
    },
    data () {
      return {
        orderId: '',
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
          accountName: {
            rules: [
              { required: true, message: '请输入账户名称!'},
            ]
          },
          cardNo: {
            rules: [
              { required: true, message: '请输入账号!'},
            ]
          },
          openBank: {
            rules: [
              { required: true, message: '请输入开户行!'},
            ]
          },
          unionpayNo: {
            rules: [
              { required: true, message: '请输入联行号!'},
            ]
          },
          types: {
            rules: [
              { required: true, message: '请选择账户类型!'},
            ]
          },
          companyId: {
            rules: [
              { required: true, message: '请选择所属公司!'},
            ]
          },
        },
        url: {
          add: "/dictionary/lcDicBankCard/add",
          edit: "/dictionary/lcDicBankCard/edit",
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        console.log('添加')
        //this.edit({});
        this.form.resetFields();
        this.model = Object.assign({});
        this.visible = true;
        this.model.companyId = this.orderId
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'accountName','cardNo','openBank','unionpayNo','openProvince','openCity','types','delFlag','companyId'))
        })
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'accountName','cardNo','openBank','unionpayNo','openProvince','openCity','types','delFlag','companyId'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
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
        this.form.setFieldsValue(pick(row,'accountName','cardNo','openBank','unionpayNo','openProvince','openCity','types','delFlag','companyId'))
      },
      //新增时初始化主键
      initId(orderId){
        this.orderId = orderId
      }
    }
  }
</script>