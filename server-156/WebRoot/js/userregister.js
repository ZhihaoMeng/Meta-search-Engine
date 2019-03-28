function Register() {

	var flag = RegisterCheck();
	if (flag) {
		var cookie = getCookieValue();
		if (cookie == null || cookie == "") {
			setCookie();
		}
		submitRequest();
	}
}

function RegisterCheck() {

	var ret = true;
	ret = checkEmail(getInputElemEmail().value)
			&& checkUsername(getInputElemUsername().value)
			&& checkPasswd(getInputElemPasswd().value) && checkRePasswd();
	return ret;
}

function submitRequest() {

	var emailadress = getInputElemEmail().value;
	var username = getInputElemUsername().value;
	var password = getInputElemPasswd().value;
	var home = getInputElemHome();
	var city = getInputElemCity();
	var job = getInputElemJob();
	var company = getInputElemCompany();
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
			var ret = xhr.responseText;
			ret = eval("(" + ret + ")");
			if (ret.result == "success") {
				document.getElementById("result-hint").innerHTML = ret.loginprompt;
				login();
			} else {
				document.getElementById("registerfail").innerHTML = ret.reason;
			}
		}
	};
	xhr.open("post", "userregister.action", false);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("username=" + username + "&password=" + password
			+ "&emailadress=" + emailadress + "&home=" + home
			+ "&city=" + city + "&job=" + job
			+ "&company=" + company);
}

function checkEmail(email) {

	var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	if (email == "") {
		document.getElementById("spanEmail").innerHTML = jsonBundle.emptyemail;
		return false;
	} else {
		if (!myreg.test(email)) {
			document.getElementById("spanEmail").innerHTML = jsonBundle.illegalemail;
			return false;
		}
	}
	clearEmailPrompt();
	return true;
}

function getInputElemUsername() {
	return document.getElementById("account-input");
}

function getInputElemEmail() {
	return document.getElementById("email-input");
}

function getInputElemPasswd() {
	return document.getElementById("passwd-input");
}

function getInputElemRePasswd() {
	return document.getElementById("passwd-confirm-input");
}
function getInputElemHome(){
	return	$("#home-input").val();
}

function getInputElemCity(){
	return $("#city-input").val();
}

function getInputElemJob(){
	return $("#job-input").val();
}

function getInputElemCompany(){
	return $("#company-input").val();
}
function checkUsername(strUserName) {

	var myname = /^[a-zA-Z0-9]+$/;
	if (strUserName == "") {
		document.getElementById("spanUsername").innerHTML = jsonBundle.emptyusername;
		return false;
	} else {
		if (!myname.test(strUserName)) {
			document.getElementById("spanUsername").innerHTML = jsonBundle.illegalusername;
			return false;
		}
	}
	clearUsernamePrompt();
	return true;
}

function checkPasswd(strUserpassword) {

	if (strUserpassword == "") {
		document.getElementById("spanPasswd").innerHTML = jsonBundle.emptypasswd;
		return false;
	} else {
		if (strUserpassword.length > 16 || strUserpassword.length < 6) {
			document.getElementById("spanPasswd").innerHTML = jsonBundle.passwdlenerr;
			return false;
		}
	}
	clearPasswdPrompt();
	return true;
}

function checkRePasswd() {

	var strRePasswd = getInputElemRePasswd().value;
	var strPasswd = getInputElemPasswd().value;
	if (strRePasswd == "") {
		document.getElementById("spanRePasswd").innerHTML = jsonBundle.emptyrepasswd;
		return false;
	} else {
		if (strRePasswd != strPasswd) {
			document.getElementById("spanRePasswd").innerHTML = jsonBundle.diffrepasswd;
			return false;
		}
	}
	clearPasswdPrompt();
	return true;
}

function clearFailDiv() {
	document.getElementById("registerfail").innerHTML = "";
}

function clearEmailPrompt() {
	document.getElementById("spanEmail").innerHTML = "";
}

function clearUsernamePrompt() {
	document.getElementById("spanUsername").innerHTML = "";
}

function clearPasswdPrompt() {
	document.getElementById("spanPasswd").innerHTML = "";
}

function clearPasswdConfirmPrompt() {
	document.getElementById("spanRePasswd").innerHTML = "";
}

function getFocusEmail() {
	clearFailDiv();
	clearEmailPrompt();
}

function loseFocusEmail() {

	var emailInput = document.getElementById("email-input");
	var emailPrompt = document.getElementById("spanEmail");
	var strEmail;
	if (null != emailInput)
		strEmail = emailInput.value;
	if (null != strEmail && strEmail.length > 0) {
		checkEmail(strEmail);
	}
}

function getFocusUsername() {
	clearFailDiv();
	clearUsernamePrompt();
}

function loseFocusUsername() {
	var myname = /^[a-zA-Z0-9]+$/;
	var usernameInput = getInputElemUsername();
	var strUserName;
	if (null != usernameInput)
		strUserName = usernameInput.value;
	if (null != strUserName && strUserName.length > 0
			&& !myname.test(strUserName)) {
		document.getElementById("spanUsername").innerHTML = jsonBundle.illegalusername;
	}
}

function getFocusPasswd() {
	clearFailDiv();
	clearPasswdPrompt();
}

function loseFocusPasswd() {

	var passwdInput = document.getElementById("passwd-input");
	var strPasswd = passwdInput.value;
	var passwdLen = strPasswd.length;
	if (passwdLen > 0) {
		if (passwdLen < 6 || passwdLen > 16) {
			document.getElementById("spanPasswd").innerHTML = jsonBundle.passwdlenerr;
		} else if (strPasswd.indexOf(" ") != -1) {
			document.getElementById("spanPasswd").innerHTML = jsonBundle.spaceinpasswd;
		}
	}
}

function getFocusPasswdConfirm() {
	clearFailDiv();
	clearPasswdConfirmPrompt();
}

function loseFocusPasswdConfirm() {

	var strRePasswd = document.getElementById("passwd-confirm-input").value;
	if (strRePasswd.length > 0) {
		checkRePasswd();
	}
}

function Reset() {

	var inputids = [ "email-input", "account-input", "passwd-input",
			"passwd-confirm-input" ];
	var input;
	for (var i = 0; i < inputids.length; ++i) {
		input = null;
		input = document.getElementById(inputids[i]);
		if (null != input)
			input.value = "";
	}

	var promptids = [ "spanEmail", "spanUsername", "spanPasswd", "spanRePasswd" ];
	var span;
	for (var i = 0; i < promptids.length; ++i) {
		span = null;
		span = document.getElementById(promptids[i]);
		if (null != span)
			span.innerHTML = "";
	}
}
function createCities() {
	var province = $("#address-input").val();
	$.ajax({

		url : "info.action",

		type : "post",

		dataType : "json",
		data : {pid : province},
		success : createCities2,

		error : function(data) {
			alert("error");
		}

	});
}

function createCities2(json) {

	// var cities=city2.split(",");
	//
	var s_root = document.getElementById("city-input");
	s_root.options.length = 0;
	$("#city-input").append("<option value=''>..市/地区..</option>");
	$.each(json, function(idx, item) {
		var city = item[1];
		var cityid = item[0];
		$("#city-input").append(
				"<option value='" + cityid + "'>" + city + "</option>");
	});
	//
	// for(i=0;i<cities.length;i++){
	//
	// var city=cities[i];
	//
	// $('#s2').append("<option value='city'>"+city+"</option>");

	// }

}

function createCompanies() {
	var job = $("#job-input").val();
	var city = $("#city-input").val();
	$.ajax({
		url : "registercompany.action",
		type : "post",
		dataType : "json",
		data : {
			jobid : job,
			addressid : city
		},
		success : function(data) {
			var s_root = document.getElementById("company-input");
			s_root.options.length = 0;
			$("#company-input").append("<option value=''>..选择公司..</option>");
			$.each(data, function(idx, item) {
				var company = item;
				$("#company-input").append(
						"<option value='" + company + "'>" + company
								+ "</option>");
			});
			$("#company-input").append("<option value='其他'>其他</option>");
		}
	})
}