<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- jsp文件头和头部 -->
<%@ include file="../system/index/top.jsp"%>
<!-- 日期框 -->
<link rel="stylesheet" href="static/ace/css/datepicker.css" />
<style type="text/css">
p {
	padding-top: 7px;
}
</style>
</head>

<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="hr hr-18 dotted hr-double"></div>
					<div class="row">
						<div class="col-xs-12">

							<!-- 存放生成的hmlt开头  -->
							<!-- <form class="form-horizontal" role="form"> -->
							<form action="goods/${msg}.do" name="Form" id="Form"
								method="post" class="form-horizontal">
						
								<div class="col-md-12" >
									<div class="form-group" style="margin-bottom: 0">
									<input type="hidden" id="order_id" name="order_id" value="${pd.order_id}"/>
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">商城单号：</label>
										<div class="col-sm-9" style="width:20%">
											<p>${pd.order_id}</p>
										</div>
										<c:forEach items="${record}" var="record">
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">${record.record_note}：</label>
										<div class="col-sm-9"  style="width:20%">
											<p>${record.addtime}</p>
										</div>
										</c:forEach>
										
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">用户昵称：</label>
										<div class="col-sm-9" style="width:20%">
											<p>${pd.username}</p>
										</div>
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">用户手机：</label>
										<div class="col-sm-9"style="width:20%" >
											<p>${pd.phone}</p>
										</div>
									</div>
							
								
									<div class="form-group" style="margin-bottom: 0"> 
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">收货地址：</label>
										<div class="col-sm-9">
											<p><input id="addr_realname" name ="addr_realname" size="4" value="${pd.addr_realname}"/>
											<input id="addr_phone" name ="addr_phone" size="12" value="${pd.addr_phone}"/>
											<input id="addr_city" name ="addr_city" size="20" value="${pd.addr_city} "/>
											<input id="address" name ="address" size="44" value="${pd.address}"/>
											</p>
										</div>
									</div>
									
									<div class="form-group" style="margin-bottom:10px">
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">选择快递：</label>
										<div class="col-sm-9" style="width:20%">
								  	 <select class="chosen-select form-control" name="express_title" id="express_title" onchange="dh()" style="height:27px;width:200px;margin-top: 5px">
								  	 <option value="">请选择</option>
								  	<option value="wxkd" <c:if test="${'wxkd' eq pd.express_title}">selected</c:if>>无需快递</option>
								  	 <c:forEach items="${express}" var="express">
								  	 <option value="${express.express_title}" <c:if test="${'wxkd' ne pd.express_title}"><c:if test="${express.express_title eq pd.express_title}">selected</c:if></c:if> >${express.express_name}</option>
								  	 </c:forEach>
								  	 </select>
									</div>
									<div id="dh" <c:if test="${'wxkd' eq pd.express_title}">style="display:none"</c:if>>
									<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1">快递单号：</label>
										<div class="col-sm-9"style="width:20%" >
											<input type="text" id="express_num"  value="${pd.express_num}" style="height: 27px;width:180px;margin-top: 5px"> 
											<c:if test="${!empty pd.express_num}">
											<a  id="getdiv"  data-toggle="modal" data-target="#myModal">查询</a><!--  -->
											</c:if>
										</div>
									</div>		
								</div>
								</div>
								<div class="col-md-12">
									<div class="form-group">
										<label class="col-sm-3 control-label no-padding-right"
											for="form-field-1"></label>
										<div class="col-sm-9">
											<c:if test="${pd.status==1||pd.status==2}">
											<a class="btn btn-mini btn-primary" onclick="send('${pd.order_id}')">发货</a>
											</c:if>
											<a class="btn btn-mini btn-danger"
												onclick="window.location.href = document.referrer;">返回</a>

										</div>
									</div>
								</div>
								<table id="simple-table" class="table table-striped table-bordered table-hover" style="margin-top:5px;">
								<thead>
								<tr>
									
									<th class="center">商品</th>
									<th class="center">单价</th>
									<th class="center">数量</th>
									<th class="center">售后</th>
									<th class="center">状态</th>
									<th class="center">优惠</th>
									<th class="center">运费</th>
									<th class="center">实收款</th>
								</tr>
							</thead>
								
								<c:forEach items="${orderdetail}" var="order" varStatus="os">
								<tr>
								<td style="width:360px">
											<div style="float:left;width:60px;text-align: center;"><img src="${order.goods_pic}" style="width: 50px;"></div>
											<div style=""> ${order.goods_name}<p style="color:#868484;font-size: 10px ">${order.attribute_detail_name}<input type="hidden" id="orderdetail_id" name="orderdetail_id" value="${order.order_detail_id}"/></p></div>
											</td>
											<td class='center' style="width:100px;vertical-align:middle;"><input id="goods_price" name="goods_price" value="${order.goods_price}"/></td>
											<td class='center' style="width:100px;vertical-align:middle;"><input id="goods_count" name="goods_count" value="${order.goods_count}"/></td>
											<td class='center' style="width:100px;vertical-align:middle;">
											<a href="refund/torefund?order_detail_id=${order.order_detail_id}">
											<c:if test="${order.status==3}">待退款</c:if>
											<c:if test="${order.status==4}">退款完成</c:if>
											</a>
											</td>
											<td class='center' style="width:100px;vertical-align:middle;<c:if test="${os.count!=pd.detaillength}"> border-bottom: 1px solid #f5f5f5;</c:if>">
											<input type="hidden" id="status" name="status" value="${order.status}"/>
											<c:if test="${os.index==0}">
											<c:if test="${order.status==0}">
											待付款
											</c:if>
											<c:if test="${order.status==1}">
											待发货
											</c:if>
											<c:if test="${order.status==2}">
											已发货
											</c:if>
											<c:if test="${order.status==3}">
											待退款
											</c:if>
											<c:if test="${order.status==4}">
											退款成功
											</c:if>
											<c:if test="${order.status==5||order.status==6}">
											交易成功
											</c:if>
											</c:if> 
											</td>
											<td class='center' style="width:100px;vertical-align:middle;<c:if test="${os.count!=pd.detaillength}"> border-bottom: 1px solid #f5f5f5;</c:if>"><input id="coupon_price" name="coupon_price" value="${order.coupon_price}"/></td>
											<td class='center' style="width:100px;vertical-align:middle;<c:if test="${os.count!=pd.detaillength}"> border-bottom: 1px solid #f5f5f5;</c:if>"><input id="freight_price" name="freight_price" value="${order.freight_price}"/></td>
											<td class='center' style="width:100px;vertical-align:middle;<c:if test="${os.count!=pd.detaillength}"> border-bottom: 1px solid #f5f5f5;</c:if>">
											<c:if test="${os.index==0}"><input id="order_total" name="order_total" value="${pd.order_total}"/></c:if>
											</td>
										</tr>
								</c:forEach>
								</table>
 
  
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
    <div class="modal-dialog" role="document">  
        <div class="modal-content">  
            <div class="modal-header">  
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">  
                    <span aria-hidden="true">×</span>  
                </button>  
                <h4 class="modal-title" id="myModalLabel">${pd.express_name}：${pd.express_num}</h4>  
            </div>  
            <div class="modal-body" id="show-express">  
                <p>loading…</p>  
            </div>  
            <div class="modal-footer">  
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>  
            </div>  
        </div>  
    </div>  
</div> 
<div style="text-align:right">
<a class="btn btn-light btn-xs" onclick="tosearch();" > 保存</a>
</div>
							</form>
							<!-- 存放生成的hmlt结尾 -->

						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->


		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up"
			class="btn-scroll-up btn btn-sm btn-inverse"> <i
			class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>

	<script type="text/javascript">

		function send(order_id) {
			var express_title = $('#express_title').val();
			var express_name = encodeURIComponent($('#express_title').find(
					"option:selected").text());
			var express_num = $('#express_num').val();
			if (express_title == '') {
				alert('请选择快递');
				return;
			}

			if (express_title != 'wxkd') {
				if (express_num == '') {
					alert('请填写快递单号');
					return;
				}
			}
			$.ajax({
				url : 'order/send',
				type : 'post',
				data : {
					order_id : order_id,
					express_name : express_name,
					express_title : express_title,
					express_num : express_num
				},
				success : function(data) {
					alert(data.message);
					if (data.result == 1) {
						window.location.href = document.referrer;
					}
				}
			})
		}

		function dh() {
			var express_title = $('#express_title').val();
			if (express_title == 'wxkd') {
				$('#dh').hide();
			} else {
				$('#dh').show();
			}
		}
		
			$('#myModal').on('shown.bs.modal', function (e) {  
	        	var express_title = $('#express_title').val();
	 			var express_num = $('#express_num').val();
	 			$.ajax({
	 				url : 'express/info',
	 				type : 'post',
	 				data : {
	 					express_title : express_title,
	 					express_num : express_num
	 				},
	 				success : function(data) {
	 					if (data.Success == true) {
	 						$('#show-express').html('');
	 						if(data.State==0){
	 							$('#show-express').html('<p>'+data.Reason+'</p>');
	 							return;
	 						}
	 						$.each(data.Traces,function(i,item){
	 							$('#show-express').append('<p>'+item.AcceptTime+'&nbsp;&nbsp;'+item.AcceptStation+'</p>');
	 						})
	 					}
	 					else{
	 						$('#show-express').html('<p>'+data.Reason+'</p>');
	 					}
	 				}
	 			})
	         });


			function tosearch() {
				var status =$('#status').val();
				var order_id =$('#order_id').val();
				var addr_realname =$('#addr_realname').val();
				var addr_phone =$('#addr_phone').val();
				var addr_city =$('#addr_city').val();
				var address =$('#address').val();
				var order_total =$('#order_total').val();
				var cellNums = document.getElementById("simple-table").rows[0].cells.length;
				//表格的行数
		        var rowNums = document.getElementById("simple-table").rows.length; 
		        var orderdetail_ids = new Array();
		        var goods_prices = new Array();
		        var goods_counts = new Array();
		        var coupon_pricess = new Array();
		        var freight_prices = new Array();
		        for(var m=1;m<rowNums;m++){
		        	orderdetail_ids[m-1] = document.getElementById("simple-table").rows[m].cells[0].getElementsByTagName("input")[0].value;
		        }
		        for(var m=1;m<rowNums;m++){
		        	goods_prices[m-1] = document.getElementById("simple-table").rows[m].cells[1].getElementsByTagName("input")[0].value;
		        }
		        for(var m=1;m<rowNums;m++){
		        	goods_counts[m-1] = document.getElementById("simple-table").rows[m].cells[2].getElementsByTagName("input")[0].value;
		        }
		        for(var m=1;m<rowNums;m++){
		        	coupon_pricess[m-1] = document.getElementById("simple-table").rows[m].cells[5].getElementsByTagName("input")[0].value;
		        }
		        for(var m=1;m<rowNums;m++){
		        	freight_prices[m-1] = document.getElementById("simple-table").rows[m].cells[6].getElementsByTagName("input")[0].value;
		        }
				if(status != 0)  
				{
					alert('订单已支付不能修改相关信息！！');
					document.location.reload();
					return;
				}
				var GOODS_IDS='';
				var GOODS_PRICES='';
				var GOODS_COUNTS='';
				var COUPON_PRICESS='';
				var FREIGHT_PRICES='';
		        for(var m=0;m<orderdetail_ids.length;m++)
			      {
		        	var re = /^[0-9]+.?[0-9]*$/;  
		        	var price = goods_prices[m];
		        	var counts = goods_counts[m];
		        	var coupon = coupon_pricess[m];
		        	var freight = freight_prices[m];
		        	var id = orderdetail_ids[m];
		        	if(!re.test(price) || !re.test(counts) || !re.test(coupon) || !re.test(freight))
			        {
		        		alert('修改价格和数量请注意规范！！');
	                    document.location.reload();
	                    return;
				     }else
					 {
				    	 if(GOODS_IDS=='') GOODS_IDS += id;
	                        else GOODS_IDS += ',' + id;
				    	 if(GOODS_PRICES=='') GOODS_PRICES += price;
	                        else GOODS_PRICES += ',' + price;
				    	 if(COUPON_PRICESS=='') COUPON_PRICESS += coupon;
	                        else COUPON_PRICESS += ',' + coupon;
				    	 if(GOODS_COUNTS=='') GOODS_COUNTS += counts;
	                        else GOODS_COUNTS += ',' + counts;
				    	 if(FREIGHT_PRICES=='') FREIGHT_PRICES += freight;
	                        else FREIGHT_PRICES += ',' + freight;
				    }
				  }
				alert(GOODS_IDS);
				$.ajax({
	                url : 'order/update',
	                type : 'post',
	                data : {
	                	order_total : order_total,
	                	address : address,
	                	addr_city : addr_city,
	                	addr_phone : addr_phone,
	                	addr_realname : addr_realname,
	                	order_id : order_id,
	                	GOODS_IDS: GOODS_IDS,
	                	GOODS_PRICES : GOODS_PRICES,
	                	GOODS_COUNTS : GOODS_COUNTS,
	                	COUPON_PRICESS : COUPON_PRICESS,
	                	FREIGHT_PRICES : FREIGHT_PRICES
	                },
	                dataType:'json',
	                cache: false,
	                success : function(data) {
	                    alert(data.message);
	                    if (data.result == 1) {
	                        window.location.href = document.referrer;
	                    }
	                }
	            })
	           
	        }
	</script>


</body>
</html>