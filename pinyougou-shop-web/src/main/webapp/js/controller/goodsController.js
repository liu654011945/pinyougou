 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,uploadService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	

	

	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//添加

	$scope.add=function () {
		//先取出富文本编辑器中的内容 赋值给变量
		var htmltext = editor.html();
		$scope.entity.goodsDesc.introduction=htmltext;
        goodsService.add( $scope.entity  ).success(
        	function (response) {//result
				if(response.success){
					$scope.entity={};//清空
					editor.html('');
				}else{
					alert(response.message);
				}
            }
		)
    }



    //上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {//Result 带有 成功之后的图片的URL
				if(response.success){
					$scope.image_entity={url:response.message};
				}else{
					alert("上传失败");
				}
            }
		)
    }

    
});	
