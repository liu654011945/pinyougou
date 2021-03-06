 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//查询所有的商品分类（默认是查询第一层）

	$scope.findByParentId =function(parentId){
        itemCatService.findByParentId(parentId).success(
        	function (response) {//List<itemcat>
				$scope.list=response;
            }
		)
	}

	$scope.grade=1;

	//点击的是调用设置级别
	$scope.setGrade=function (value) {
        $scope.grade=value;
    }

	$scope.selectList=function (p_entity) {
		//根据当前的等级 进行赋值
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}

		//等级是2  将变量1赋值（变量1绑定第二级面包屑）
		if($scope.grade==2){
            $scope.entity_1=p_entity;
            $scope.entity_2=null;
		}
		//等级是3  将变量2赋值（变量1绑定第三级面包屑）
		if($scope.grade==3){
            $scope.entity_2=p_entity;
		}

        $scope.findByParentId(p_entity.id);
		
    }
    
});	
