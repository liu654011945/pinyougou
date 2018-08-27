var app = angular.module('pinyougou', []);//定义模块


//过滤器
app.filter('trustHtml',function ($sce) {
    return function (data) {//data 就是原来的html的代码
        return $sce.trustAsHtml(data);//经过过滤的代码
    }
})