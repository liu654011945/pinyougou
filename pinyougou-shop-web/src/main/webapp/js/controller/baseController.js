 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	
	//将一个json字符创转成 指定的格式（以某一个数组里面的对象的属性的值 通过“，”拼接起来）
	
	$scope.jsonStringToString=function (jsonstring,key) {
		//object={}
		//object.id
		//object['id']
		//1.json字符串转成JSON对象
		var fromJson = angular.fromJson(jsonstring);//[{id:1,text:222},{}]
		var str = "";
		for(var i=0;i<fromJson.length;i++){
			var object=fromJson[i];//{id:1,text:2}
           str += object[key]+",";
		}

		if(str.length>=1){
           str= str.substring(0,str.length-1);
		}
		return str;
    }
	
});	