<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        
        <a-form-item label="城市等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <!-- <a-input v-decorator="['level']" placeholder="请选择地市编码"></a-input> -->
          <j-dict-select-tag v-decorator="['level',validatorRules.level]" :triggerChange="true" placeholder="请选择城市等级" dictCode="city_level"/>
        </a-form-item>

        <a-form-item label="已选城市" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-textarea placeholder="请输入备注" v-decorator="['cityName']" :disabled="true" />
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JTreeSelect from '@/components/jeecg/JTreeSelect'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  
  export default {
    name: "LcDicCityModal",
    components: { 
      JTreeSelect,
      JDictSelectTag,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        level: '',
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
          level:{rules: [{ required: true, message: '请选择城市等级!' }]},
         
        },
        url: {
          batchSetLevel: "/dictionary/lcDicCity/batchSetLevel",
          edit: "/dictionary/lcDicCity/edit",
        },
        expandedRowKeys:[],
        pidField:"parentCode",
        cityName :[],
      }
    },
    created () {
      var ids = []
    },
    methods: {
     
      edit (ids,cityName) {
        this.ids = ids;
        this.form.resetFields();
        
        //cityName = "beijing";
        this.visible = true;
        let name = cityName.join(',');
        console.log(name);
        this.$nextTick(() => {
          let modal = {'cityName':name};
          this.form.setFieldsValue(pick(modal,"cityName"))
        })
        
        
      },
      close () {
        this.$emit('close');
        this.level = ''
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            values['cityIds'] = this.ids;
           
            that.confirmLoading = true;
            let httpurl = this.url.batchSetLevel;
            let method = 'put';
            
            let formData = Object.assign(this.model, values);
           
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
               // that.parent.loadData(1);
                this.$emit('ok',formData,false);
                //that.submitSuccess(formData,old_pid==new_pid)
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
        console.log("popupCallback -> row", row)
        this.form.setFieldsValue(pick(row,'cityName','cityCode','parentCode','level'))
      },
      submitSuccess(formData,flag){
        if(!formData.id){
          let treeData = this.$refs.treeSelect.getCurrTreeData()
          this.expandedRowKeys=[]
          this.getExpandKeysByPid(formData[this.pidField],treeData,treeData)
          this.$emit('ok',formData,this.expandedRowKeys.reverse());
        }else{
          this.$emit('ok',formData,flag);
        }
      },
      getExpandKeysByPid(pid,arr,all){
        if(pid && arr && arr.length>0){
          for(let i=0;i<arr.length;i++){
            if(arr[i].key==pid){
              this.expandedRowKeys.push(arr[i].key)
              this.getExpandKeysByPid(arr[i]['parentId'],all,all)
            }else{
              this.getExpandKeysByPid(pid,arr[i].children,all)
            }
          }
        }
      }
      
    }
  }
</script>