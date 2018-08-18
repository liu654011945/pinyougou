app.service('uploadService', function ($http) {

    this.uploadFile = function () {
        //创建一个formData对象（对应就是表单）
        var formData = new FormData();
        //追加一个字段 name=file  <input type="file" name="file">
        //第一个file 一定是要和后台的controller中的接收的参数名一致
        //第二参数file 是指定的前端的input type ="file" id="file" 中的id的值 这里取第一个图片对象
         formData.append("file", file.files[0]);
        return $http({
            method: 'POST',
            url: "../uploadFile.do",
            data: formData,
            headers: {'Content-Type': undefined},//headers 用于设置媒体类型 entype="multi/data" 默认会angularjs会追加媒体类型
            transformRequest: angular.identity // 使用angurlajs定义的请求
        });
    }
})