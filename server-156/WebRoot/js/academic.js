function AcademicAction(){
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	
	document.forms.myForm.action="academic.action";
	var elem= document.getElementById("page");
	if(null!=elem) elem.value = 1;//首页提交新请求时需要将page置为1
	if(null!=elem) elem.value="";
	document.forms.myForm.submit();
	
}
function filtBySandE(){
	var start = document.getElementById("leftnav_input_ylo");
	var end = document.getElementById("leftnav_input_yhi");
	filtFromStoE(start.value,end.value);
}
function filtByYear(end){
	
	filtFromStoE(0,end);
}
function filtFromStoE(start,end){
	alert(start+" "+end);
	
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	
	document.forms.myForm.action="academic.action";
	var istart = document.getElementById("startFilter");
	if(null!=istart) istart.value = start;
	var iend = document.getElementById("endFilter");
	if(null!=iend) iend.value = end;
	document.forms.myForm.submit();
}
function rankFilter(){
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	var obj = document.getElementById("rankSelect");
	var value = obj.value;
	document.forms.myForm.action="academic.action";
	var elem= document.getElementById("rankFilter");
	if(null!=elem) elem.value = value;//首页提交新请求时需要将page置为1
	document.forms.myForm.submit();
}

function searchButtonClick(evt){
	evt = evt||window.event; //兼容IE和Firefox获得keyBoardEvent对象
	 var key = evt.keyCode || evt.charCode;//兼容IE和Firefox获得keyBoardEvent对象的键值 
	 
	     if(key == 13){   
	    	var button =document.getElementById("searchbutton");  
	 		button.click();
	}
}

function acaPageAction(pageNo){
	
	if (isNaN(parseInt(pageNo))) pageNo = 1;
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	document.forms.myForm.action="academic.action";
	var elem= document.getElementById("page");
	if(null!=elem) elem.value = pageNo;
	document.forms.myForm.submit();
}

