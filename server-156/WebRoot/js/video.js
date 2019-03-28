function VideoAction(){
	if (getCookieValue() ==""||getCookieValue() ==null) 
	{
        setCookie();
	}
	window.location.href='video.jsp';
	document.getElementById("page").value = 1;//首页提交新请求时需要将page置为1
	document.PictureForm.submit();
}

