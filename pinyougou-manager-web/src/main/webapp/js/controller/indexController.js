app.controller('indexController',function ($scope,loginService) {
    $scope.getLoginName=function () {
        loginService.getLoginName().success(
            function (response) {//Map
                $scope.loginName=response.loginName;
            }
        )
    }
})