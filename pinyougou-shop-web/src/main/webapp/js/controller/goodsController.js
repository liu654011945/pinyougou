 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,typeTemplateService,itemCatService,uploadService,goodsService){
	
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


    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]}
    //上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {//Result 带有 成功之后的图片的URL
				if(response.success){
					$scope.image_entity.url=response.message;
				}else{
					alert("上传失败");
				}
            }
		)
    }
    $scope.addImageEntity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }



    //查询一级分类列表
	$scope.findItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {//List<tbitemCat>
				$scope.itemCat1List=response;
            }
		)
    }

    //监控 某一个变量(一级分类的ID的变化)的变化

	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        //当一级分类改变的时候，获取新值 调用查询二级分类列表的方法
		if(newValue!=null && newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemCat>
                    $scope.itemCat2List=response;
                }
            )
		}
    })


    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        //当一级分类改变的时候，获取新值 调用查询二级分类列表的方法
        if(newValue!=null && newValue!=undefined){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List<tbitemCat>
                    $scope.itemCat3List=response;
                }
            )
        }
    })

	//取从分类数据中获取模板iD
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        //当一级分类改变的时候，获取新值 调用查询二级分类列表的方法
        if(newValue!=null && newValue!=undefined){
            itemCatService.findOne(newValue).success(
                function (response) {//itemcat
                    $scope.entity.goods.typeTemplateId=response.typeId;
                }
            )
        }
    })


    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        //当一级分类改变的时候，获取新值 调用查询二级分类列表的方法
        if(newValue!=null && newValue!=undefined){
            typeTemplateService.findOne(newValue).success(
                function (response) {//typeTemplate
                    $scope.typeTemplate=response;
                    $scope.typeTemplate.brandIds=angular.fromJson($scope.typeTemplate.brandIds);
                    $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.typeTemplate.customAttributeItems);
                }
            );

            //同时调用该模板的id对应的模板的数据里面的规格数据（重新组织查询出来）

			typeTemplateService.findSpecList(newValue).success(
				function (response) {//List<Map>  [{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
					$scope.specList=response;
                }
			)



        }
    });


	//当点击复选框的时候调用 影响变量(specificationItems)的值
  /*  $scope.entity.goodsDesc.specificationItems=[
        {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
        {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
    ]*/

    $scope.updateSpecAttribute=function ($event,specName,specValue) {//specName --》网络 specValue-->对应的就是你点击的选项的值 :移动4G

		//获取对象的方法  从已有的数组中获取里面的对象
		var object =$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,specName,"attributeName");
		//object = {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}
		if(object!=null){
            //如果点击的选项 对应的对象有 ，那么就需要直接添加选项的值

			if($event.target.checked){
                object.attributeValue.push(specValue);
            }else{
				//取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(specValue),1);

                if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
            //如果点击的选项 对应的对象没有，就直接添加一个对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeValue":[specValue],"attributeName":specName});
		}
    }

    //方法是重新生成商品SKU的列表 在点击复选框的时候调用从头重新生成SKU列表
    $scope.createItemList=function () {
		//初始化：
        $scope.entity.itemList=[{spec:{},price:9999,num:999,status:'0',isDefault:'0'}];

        //循环遍历变量specificationItems
		var specifcationItems =  $scope.entity.goodsDesc.specificationItems;
		//[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},{"attributeValue":["16G","32G"],"attributeName":"机身内存"}]
		for(var i=0;i<specifcationItems.length;i++){
			//这个方法就是需要循环遍历  网络 再遍历 移动3G  最终拼接对象
            $scope.entity.itemList=addColumn($scope.entity.itemList,specifcationItems[i].attributeName,specifcationItems[i].attributeValue);
		}

    }

    /**
	 *
     * @param list [{spec:{},price:9999,num:999,status:'0',isDefault:'0'}]
     * @param columnName  网络
     * @param columnValues  ["移动3G","移动4G"]
     * @returns {Array}
     */
    addColumn=function (list,columnName,columnValues) {
			var newList = [];
			for(var i=0;i<list.length;i++){
                var oldRow= list[i];//{spec:{},price:9999,num:999,status:'0',isDefault:'0'}
				for(var j=0;j<columnValues.length;j++){//["移动3G","移动4G"]
					var newRow=angular.fromJson(angular.toJson(oldRow));
					newRow.spec[columnName]=columnValues[j];
					newList.push(newRow);
				}
			}
			return newList;
    }

    
});	
