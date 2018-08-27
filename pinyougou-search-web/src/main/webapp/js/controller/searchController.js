app.controller('searchController', function ($scope, searchService) {
    //写一个方法 用来根据写的主查询条件 调用service的方法 获取结果 结果通过遍历展示出来

    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '', spec: {}};

    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {//Map resultMap
                $scope.resultMap = response;
            }
        );
    }
    //点击分类 或者品牌的时候需要调用一个方法 去改变变量（传递后台去的条件查询的对象）的值
    $scope.addSearchItem=function (key,value) {
        //影响变量的值
        if(key=='category' || key=='brand'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }
    
    $scope.removeSearchItem=function (key) {
        if(key=='category' || key=='brand'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
})