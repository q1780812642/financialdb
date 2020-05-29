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

        <a-form-item label="地市名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['cityName',validatorRules.cityName]" placeholder="请输入地市名称"></a-input>
        </a-form-item>
        <a-form-item label="地市编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['cityCode',validatorRules.cityCode]" placeholder="请输入地市编码"></a-input>
        </a-form-item>
        <a-form-item label="城市等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <!-- <a-input v-decorator="['level']" placeholder="请选择地市编码"></a-input> -->
          <j-dict-select-tag v-decorator="['level']" :triggerChange="true" placeholder="请选择城市等级" dictCode="city_level"/>
        </a-form-item>
        <a-form-item label="父级编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-tree-select
            ref="treeSelect"
            placeholder="请选择父级编码"
            v-decorator="['parentCode',,validatorRules.parentCode]"
            dict="lc_dic_city,city_name,city_code"
            pidField="parent_code"
            pidValue="0"
            hasChildField="has_child">
          </j-tree-select>
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
          cityName:{rules: [{ required: true, message: '请输入地市名称!' }]},
          cityCode:{rules: [{ required: true, message: '请输入地市编码!' }]},
          parentCode:{rules: [{ required: true, message: '请选择父级编码！'}]}
        },
        url: {
          add: "/dictionary/lcDicCity/add",
          edit: "/dictionary/lcDicCity/edit",
        },
        expandedRowKeys:[],
        pidField:"parentCode"
     
      }
    },
    created () {
    },
    methods: {
      add (obj) {
        this.edit(obj);
      },
      edit (record) {
        console.log('编辑')
        console.log(record)
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'cityName','cityCode','parentCode','level'))
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
            let old_pid = this.model[this.pidField]
            let formData = Object.assign(this.model, values);
            let new_pid = this.model[this.pidField]
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.submitSuccess(formData,old_pid==new_pid)
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