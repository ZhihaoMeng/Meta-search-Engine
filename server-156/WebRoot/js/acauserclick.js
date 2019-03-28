//利用AJAX记录用户点击
function userclick(title, abstr, year, sources, link, like, citecount, authors, score) {
	if (getCookieValue("Cookieid") == "" || getCookieValue("Cookieid") == null) {
		setCookie();
	}

	var xhr;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xhr=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xhr=new ActiveXObject("Microsoft.XMLHTTP");
	  }

	//var xhr = createXMLHttpRequest();

	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
			var ret = xhr.responseText;
			console.log(ret);
			ret = eval("(" + ret + ")");
			console.log(ret.flag);
			if (ret.flag == "true") {
				console.log(ret.url);
				//window.open(ret.url);
			}

		}
	};
	var url="cookieid=" + getCookieValue("Cookieid") + "&query="
	+ document.getElementById("query").value + "&title=" + title
	+ "&abstr=" + abstr + "&link=" + encodeURI(link)
	+ "&sources=" + sources + "&year=" + year + "&likeOrDislike=" + like + "&citeCount=" + citecount + "&authors=" + authors + "&score=" + score;
	xhr.open("post", "acauserclick.action", false);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send(url);
}