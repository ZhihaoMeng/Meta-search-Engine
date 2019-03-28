<%@page import="common.entities.searchresult.RelateSearchWord"%>
<%@page import="com.sun.accessibility.internal.resources.accessibility"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="server.info.config.VisibleConstant.ContentNames"%>
<%@page import="server.info.config.VisibleConstant"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.*"%>
<%@page import="server.info.config.LangEnvironment.LangEnv"%>
<%@page import="server.info.config.LangEnvironment.ClientType"%>
<%@page import="server.info.config.SessionAttrNames"%>
<%@page import="server.info.config.LangEnvironment"%>
<%@page import="server.info.config.PicturePath.PictureType"%>
<%@page import="server.info.config.PicturePath"%>
<%@page import="org.json.JSONObject"%>
<%@page import="struts.actions.web.JsDataBundler" %>
<%@page import="common.entities.searchresult.AcademicResult"%>
<%@page import="org.jfree.chart.servlet.ServletUtilities"%>
<%@page import="common.functions.webpagediagram.PieChartPainter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!
	private final static int MAX_ABSTR_LENGTH = 100;//最大显示摘要的长度
	private final static int PIE_WIDTH=550;//生成的饼图的宽
	private final static int PIE_HEIGHT=450;//生成的饼图的高
	private final static double PIE_IMG_FACTOR=0.6;//实际上在页面显示时，img元素的宽高与饼图宽高的倍数
	private final String limitAbstr(String orgin) {
		if (null == orgin || orgin.length() <= MAX_ABSTR_LENGTH)
			return orgin;
		return orgin.substring(0, MAX_ABSTR_LENGTH) + "...";
	}%>
<%
//用户兴趣信息
	String username = (String)session.getAttribute(SessionAttrNames.USERNAME_ATTR);
	String word=(String)request.getAttribute("query");
	if(null==word) word="";
	String encodedQuery=URLEncoder.encode(word);//由于查询词会用于拼接在成员搜索引擎结果页面的URL然后放在html元素中，查询词如果带有引号会导致错误，所以要处理
	Integer curPage=(Integer)request.getAttribute("page");
	LangEnv lang=(LangEnv) session.getAttribute(SessionAttrNames.LANG_ATTR);
	if(null==lang) lang=LangEnvironment.currentEnv(ClientType.web);
	List<RelateSearchWord> relatedSearch = (List<RelateSearchWord>)request.getAttribute("relatedSearch");
	List<AcademicResult> shownResult = (List<AcademicResult>)request.getAttribute("results");
	TreeMap<String,Integer> timeFilter = (TreeMap<String,Integer>)request.getAttribute("timeFilter");
	//获得一些常量信息，以JSON对象封装好并赋值给一个js变量，
	//用于浏览器中的脚本文件，使脚本修改页面时，能保持语言的正确性
	JSONObject jsdata=new JSONObject();
	JsDataBundler.getJsonForResultPageJsp(jsdata, lang);
	
	//获取饼状图（信息检索覆盖率）所需要的数据
		List<AcademicResult> allResult=(List<AcademicResult>)request.getAttribute("allResult");
		String urlPie = "";//空字符串为异常
		if(allResult != null && !allResult.isEmpty())
		{
			String fileNamePie = ServletUtilities.saveChartAsPNG(PieChartPainter.GetAcaPieChart(allResult, lang),PIE_WIDTH,PIE_HEIGHT,session);
			//ServletUtilities是面向web开发的工具类，返回一个字符串文件名,文件名自动生成，生成好的图片会自动放在服务器（tomcat）的临时文件下（temp）
			urlPie = request.getContextPath() + "/DisplayChart?filename=" + fileNamePie;
			//根据文件名去临时目录下寻找该图片，这里的/DisplayChart路径要与配置文件里用户自定义的<url-pattern>一致
		}
%>
<html>
	<script type="text/javascript"> 
		var usernameinpage=null;
		if("<%=username%>" != "null")
		{
			usernameinpage="<%=username%>";
		}
		var jsonBundle=null;
		jsonBundle=JSON.parse('<%=jsdata.toString()%>');
		window.onload=function()
		{
			rightTitle();
		};
		
		 
	</script>	
<head>
<title><%=VisibleConstant.getWebpageContent(ContentNames.page_title, lang)%></title>
<link rel="stylesheet" type="text/css" href="css/returnpage.css" />
<link rel="stylesheet" type="text/css" href="css/rpage-loginentry.css" />
<link rel="stylesheet" type="text/css" href="css/login.css" />
<link rel="stylesheet" type="text/css" href="css/style_bar.css" />
<link rel="stylesheet" type="text/css" href="css/style_score.css" />
<link rel="stylesheet" type="text/css" href="css/copyright.css" />
<link rel="stylesheet" type="text/css" href="css/academic.css" />
<script type="text/javascript" src="js/acareturnpage.js"></script>
<script type="text/javascript" src="js/academic.js"></script>
<script type="text/javascript" src="js/userlogin.js"></script>
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/XMLHttp.js"></script>
<script type="text/javascript" src="js/cookie.js"></script>
<script type="text/javascript" src="js/formAction.js"></script>
<script type="text/javascript" src="js/acauserclick.js"></script>
<script type="text/javascript" src="js/logout.js"></script>
<script type="text/javascript" src="js/picture.js"></script>
<script type="text/javascript" src="js/video.js"></script>

<script type="text/javascript"> 
	var usernameinpage=null;
	if("<%=username%>" != "null")
	{
		usernameinpage="<%=username%>";
	}
	var jsonBundle=null;
	jsonBundle=JSON.parse('<%=jsdata.toString()%>');
	window.onload=function()
	{
		rightTitle();
	};
</script>	
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<div id="academicpage-head">
		<div id="academicpage-logo" class="rpage-fleft-div">
			<a href="./academicsearch.action"><img
				src="images/IMsearch100x100.png" width="100px" height="100px"></a>
		</div>
		<div id="academicpage-head-middle" class="rpage-fleft-div">
			<div class="ss_table">
				<!--Tab标签  -->
				<span onclick="FormAction()"><%=VisibleConstant.getWebpageContent(ContentNames.tab_web, lang)%></span>
				<span onclick="PictureAction()"><%=VisibleConstant.getWebpageContent(ContentNames.tab_picture, lang)%></span>
				<span onclick="VideoAction()"><%=VisibleConstant.getWebpageContent(ContentNames.tab_video, lang)%></span>
				<span onclick="AcademicAction()"><%=VisibleConstant.getWebpageContent(ContentNames.tab_academic, lang) %></span>
			</div>
			<div id="academicsearch">
				<!--搜索框  -->
				<form name="myForm" action="academic.action" method="get">
					<input name="query" id="query" type=text size=75 maxlength=65 value="${query}" baiduSug="2" onkeydown="searchButtonClick(event)" />
					<input type=button name=btnG id="searchbutton"
						value="<%=VisibleConstant.getWebpageContent(ContentNames.search_button, lang)%>" onclick="AcademicAction()" /> 
					<input name="page" id="page" type="hidden" /> 
					<input name="rankFilter" id="rankFilter" type="hidden" />
					<input name="autoRank" id="autoRank" type="hidden"/>
					<input name="start" id="startFilter" type="hidden" />
					<input name="end" id="endFilter" type="hidden" /> 
					<input name="lang" id="lang" type="hidden" value="<%=lang%>" />
				</form>
			</div>
			<div id="result-from-pic">
				<!--结果来自于  -->
				<table>
					<tr>
						<td class="engSelCol1 engSelRow"><%=VisibleConstant.getWebpageContent(ContentNames.result_from_prompt, lang)%></td>
						<td class="engSelCol2 engSelRow"><img
							src="<%=PicturePath.getWebpageContent(PictureType.result_from_picture, lang)%>"
							width="390" height="30"></td>
					</tr>
				</table>
			</div>
		</div>
		<div id="zhuce"></div>
	</div>
	<div style="clear: both; visibility: hidden;"></div>
	<hr />
	<div class="main_content"><!-- 结果页下方三个部分 -->
		<div class="main_left"><!-- 结果页左侧的群组推荐和相关搜索部分   群组推荐暂时去掉 -->
			
				<div class="relate-opption"><!-- 左侧根据相关性、发表年限的新旧程度筛选结果 -->
				<div class="relate-filt-year-navt">筛选<img src="images/c-icon-arrow-up-gray.png" class="c-icon-arrow-up-gray"></div>
					<sapn class="sort-font">Sort By </sapn><select name="select" id="rankSelect">
							    <option value="None" selected>None</option>
							    <option value="Relevance">Relevance</option>
							    <option value="Newest First">Newest First</option>
							    <option value="Oldest First">Oldest First</option>
							</select><span class="leftnav_input_subspan" >
									<input type="submit" value="确认" class="leftnav_input_sub" onclick="rankFilter()"></span> 
				</div>
				
				<div class="rankarea">
				<div class="relate-filt-year-navt">自定义排序<img src="images/c-icon-arrow-up-gray.png" class="c-icon-arrow-up-gray"></div>
					<div id="rankChose">
		  				<div  id="rankbox1" class="rankbox" onclick="changeRank(1)">引用次数</div>
		  				<div  id="rankbox2" class="rankbox" onclick="changeRank(2)">影响因子</div><br>
		  				<div  id="rankbox3" class="rankbox" onclick="changeRank(3)">发表年限</div>
		  				<div  id="rankbox4" class="rankbox" onclick="changeRank(4)">相似性</div>
		  			</div>
		  			<div class="rankgo">
		  				<button type="button" class="btn-img" onclick="autoRank()"></button>
		  			</div>
				</div>
				
				<div class="relate-filt-year"><!-- 左侧根据发表年限范围筛选结果 -->
					<div class="relate-filt-year-navt">时间<img src="images/c-icon-arrow-up-gray.png" class="c-icon-arrow-up-gray"></div>
					<div class="relate-filt-year-navc">
						<div class="leftnav_list_cont">
							<div class="leftnav_ylo">
							<% 
							if(null != timeFilter && timeFilter.size()!=0){
							Set<String> set = timeFilter.keySet();
							for(String elem:set){%>
								<a href="#" onclick="filtByYear('<%=elem %>')"><%=elem %>（<%=timeFilter.get(elem) %>）</a>
								
								<%}} %>
							</div>
							<div class="leftnav_year_in">
									<input type="text" data-nolog="1" pattern="[0-9]*" size="4" placeholder="年" maxlength="4" name="" id="leftnav_input_ylo" class="sc-input" value="">&nbsp;-&nbsp;
									<input type="text" data-nolog="1" pattern="[0-9]*" size="4" placeholder="年" maxlength="4" name="" id="leftnav_input_yhi" class="sc-input" value="">
									<input type="hidden" name="wd" value="meta-search">
									<input type="hidden" name="ie" value="utf-8">
									<input type="hidden" name="tn" value="SE_baiduxueshu_c1gjeupa">
									<span class="leftnav_year_para" data-type="" data-cate="" data-level=""></span>
									<span class="leftnav_input_subspan" >
									<input type="submit" value="确认" class="leftnav_input_sub" onclick="filtBySandE()"></span>
									
							</div>
						</div>
					</div>
				</div>
				
				<div id="relate-search" class="left-div"><!-- 结果页左侧的相关搜索 -->
					<table class="mtable">
						<th colspan="4" style="text-align:left;"><div class="relate-filt-year-navt"><%=VisibleConstant.getWebpageContent(ContentNames.relate_search_title, lang) %><img src="images/c-icon-arrow-up-gray.png" class="c-icon-arrow-up-gray"></div></th>
							<%
								if(relatedSearch!=null&&!relatedSearch.isEmpty()){
								for(int i=0;i<relatedSearch.size();i++)
								{
									RelateSearchWord elem=relatedSearch.get(i);
									String title = elem.getWord();
									if(title.length()>15){
										title = title.substring(0, 15)+"...";
									}
									int num = elem.getNum();
							%>
							<tr class="relate-search-item">
							<%if(i<3){ %>
							<td class="relate-search-numberf"><%=i+1 %></td>
							<%}else{ %>
							<td class="relate-search-numberl"><%=i+1 %></td>
							<%} %>
							<td class="relate-search-citew" >
								<a class="relate-search-word" href="javascript:void(0)"  onclick="clickQuery(this)"><%=title%></a>
							</td>
							<td class="relate-search-cite" style="text-align:right;"><%=num %></td>
							<td class="relate-search-cite" style="text-align:left;">次检索</td>
							</tr>
							<%}}%>
						</table>
				</div>
				
		</div>
	
		<div class="main_center">
			<% 
			int count = 0;
			if(null!=shownResult&&!shownResult.isEmpty()){
			for(Iterator<AcademicResult> it=shownResult.iterator();count<10&&it.hasNext();){ 
				count++;
				int itemNum = count+(curPage-1)*10;
				AcademicResult curRes=it.next();
				if(null==curRes) continue;
				%>
				<div class="item">
					<div class="left_part"><div class="left_num"><%=itemNum %></div>
					</div>
					
					<div class="main_part">
						<div class="item_title"><a class="item_title" href="<%=curRes.getLink()%>" target="_blank" onclick="userclick('<%=curRes.getTitle()%>','<%=curRes.getAbstr()%>',' <%=curRes.getYear()%>','<%=curRes.getSource() %>','<%=curRes.getLink() %>',0,'<%=curRes.getCiteCount() %>','<%=curRes.getAuthors() %>','<%=curRes.getValue() %>')" ><%=curRes.getTitle() %></a></div>
						<div class="item_author"><%=curRes.getAuthors() %></div>
						<div class="item_abstr"><%=curRes.getAbstr() %></div>
						<div class="item_msg">Year:<%=curRes.getYear() %>&nbsp;&nbsp;&nbsp;Citation Count:<%=curRes.getCiteCount() %></div>
						<div class="item_source">全部来源：<span class="item_source_name"><%=curRes.getSource() %></span><span class="likeordislike"><span class="STYLE5">Like</span> <img id="likeshow<%=count %>" src="images/u_zan.png" align="bottom" onclick="setImgLike(<%=count %>,'<%=curRes.getTitle()%>','<%=curRes.getAbstr()%>',' <%=curRes.getYear()%>','<%=curRes.getSource() %>','<%=curRes.getLink() %>',1,'<%=curRes.getCiteCount() %>','<%=curRes.getAuthors() %>','<%=curRes.getValue() %>')" />&nbsp;&nbsp;<span class="STYLE6">&nbsp;<span class="STYLE7">Dislike</span></span> <img id="dislikeshow<%=count %>"+count src="images/u_nozan.png" align="bottom" onclick="setImgDisLike(<%=count %>,'<%=curRes.getTitle()%>','<%=curRes.getAbstr()%>',' <%=curRes.getYear()%>','<%=curRes.getSource() %>','<%=curRes.getLink() %>',-1,'<%=curRes.getCiteCount() %>','<%=curRes.getAuthors() %>','<%=curRes.getValue() %>')" /></span></div>
					</div>
					
					<div class="right_part">
		 				<a href="<%=curRes.getLink()%> target="_blank""><img src="images/pdf.png" class="item_right_img" height="164" /></a>
						<br><span class="item_source_name">&gt;&gt;Download PDF</span>		
					</div>
				</div>
				<%if(count%10==0){
					//donothing
					}else{%>
					<hr  style="width:95%;border:1px soli  #ccc" />
				<%} %>
			<% }
			}%>
			<div id="page-area" class="page_areasetting">
					
					<script language="JavaScript">
						var pg = new showPages('pg');
						pg.page = <%=curPage %>;
						pg.printHtml();
					</script>
			</div>
		</div>
		
		<div class="main_right">
			<div id="result-proportion" class="left-div">
					<div id ="fugai">
						<table class="pic-tab">
							<th class="title-hint"><%=VisibleConstant.getWebpageContent(ContentNames.result_distribution, lang) %></th>
							<% 
								if(!urlPie.equals("")) 
		   						{%>
		   					<tr><td>
		   						<img src="<%= urlPie %>" width="<%=(int)PIE_WIDTH*PIE_IMG_FACTOR %>" height="<%=(int)PIE_HEIGHT*PIE_IMG_FACTOR%>">
		   					</td></tr>
			   				<%}%>
			   			</table>
				   	</div>
				</div>
				
				
		</div>
	</div>
	<div class="copyright">
			&copy;<%=VisibleConstant.getWebpageContent(ContentNames.copyright_info, lang) %>
	</div>
	<div id="logindiv">
			<div id="loginform">
				<div id="loginform-title">
					<div id="loginform-title-text"><%=VisibleConstant.getWebpageContent(ContentNames.login_form_title, lang) %></div>
					<div id="loginform-title-quit"><span id="loginform-quit-icon" onclick="closeLoginForm()">&times;</span></div>
				</div>
				<div id="result-hint">&nbsp;</div>
				<div id="loginform-body">
					<form name="login" action="" method="post" focus="login">
						<div id="loginform-username" class="loginform-input-row">
							<span class="lf-input-left"><%=VisibleConstant.getWebpageContent(ContentNames.user_name, lang) %></span>
							<input name="username" class="loginform-input" type=text onkeydown="loginbuttonOnClick(event)" />
						</div>
						<div id="loginform-password" class="loginform-input-row">
							<span class="lf-input-left"><%=VisibleConstant.getWebpageContent(ContentNames.passwd, lang) %></span>
							<input name="password" type="password" class="loginform-input" type=text onkeydown="loginbuttonOnClick(event)" />
						</div>
						<div id="loginform-button" class="loginform-input-row">
							<input type="button" id="loginbutton" value="<%=VisibleConstant.getWebpageContent(ContentNames.login_button, lang) %>" onclick="tijiao()" />
						</div>
					</form>
				</div>
			</div>
		</div>
	<div id="mask">&nbsp;</div>
</body>
<!-- 百度搜索框提示 -->
<script charset="gbk" src="http://www.baidu.com/js/opensug.js"></script>
</html>