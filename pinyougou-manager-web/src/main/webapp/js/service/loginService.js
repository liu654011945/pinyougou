app.service('loginService',function ($http) {
    this.getLoginName=function () {
        return $http.get('/login/user/getName.do');
    }
})