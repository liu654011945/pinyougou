app.controller('indexController',function ($scope,contentService) {
    //根据分类的ID 查询分类下的广告列表  展示到页面
    $scope.contentList=[];
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {//List<content>
                $scope.contentList[categoryId]=response;//List<content>
            }
        )
    }
})