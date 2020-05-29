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

        <a-form-item label="公司编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['cyCode']" disabled placeholder="自动生成公司编码"></a-input>
        </a-form-item>
        <a-form-item label="公司名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['cyName', validatorRules.cyName]" placeholder="请输入公司名称"></a-input>
        </a-form-item>
        <a-form-item label="公司类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag v-decorator="['types']" :triggerChange="true" placeholder="请选择公司类型" dictCode="company_types"/>
        </a-form-item>
        <a-form-item label="公司联系人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['contact']" placeholder="请输入公司联系人"></a-input>
        </a-form-item>
        <a-form-item label="联系人电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['phone', validatorRules.phone]" placeholder="请输入联系人电话"></a-input>
        </a-form-item>
        <a-form-item label="公司税号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['ein']" placeholder="请输入公司税号"></a-input>
        </a-form-item>
        <a-form-item label="公司地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea placeholder="请输入公司地址" v-decorator="['address']" />
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea placeholder="请输入备注" v-decorator="['notes']" />
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
    name: "LcDicCompanyModal",
    components: { 
      JDictSelectTag,
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
          cyName: {
            rules: [
              { required: true, message: '请输入公司名称!'},
            ]
          },
          phone: {
            rules: [
              { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!'},
            ]
          },
        },
        url: {
          add: "/dictionary/lcDicCompany/add",
          edit: "/dictionary/lcDicCompany/edit",
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'cyCode','cyName','address','contact','phone','notes','ein','types','delFlag'))
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
        this.form.setFieldsValue(pick(row,'cyCode','cyName','address','contact','phone','notes','ein','types','delFlag'))
      },

      
    }
  }
</script>